#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>
#import "WeAppNativePlugin.framework/WeAppNativePlugin.h"
#import "MyPlugin.h"
# import "JPUSHService.h"
# ifdef NSFoundationVersionNumber_iOS_9_x_Max
# import <UserNotifications/UserNotifications.h>
# endif

__attribute__((constructor))
static void initPlugin() {
    [MyPlugin registerPluginAndInit:[[MyPlugin alloc] init]];
};

@implementation MyPlugin

// 声明插件ID
WEAPP_DEFINE_PLUGIN_ID(wxca742ba6781a54fd)

// 声明插件同步方法
WEAPP_EXPORT_PLUGIN_METHOD_SYNC(setBadge, @selector(setBadge:))
WEAPP_EXPORT_PLUGIN_METHOD_SYNC(getCachedMsg, @selector(getCachedMsg:))

// 声明插件异步方法
WEAPP_EXPORT_PLUGIN_METHOD_ASYNC(registerPush, @selector(handlerRegisterPush:withCallback:))

//WEAPP_EXPORT_PLUGIN_METHOD_ASYNC(myAsyncFuncwithCallback, @selector(myAsyncFunc:withCallback:))

#pragma mark - plugin registed funcs

// 开始注册
- (void)handlerRegisterPush:(NSDictionary *)param withCallback:(WeAppNativePluginCallback)callback {
    if (self.handleRegisterPushCallback) {
        callback(@{
            @"success": @NO,
            @"msg": @"privous regisetPush is not finished",
        });
        return;
    }
    self.handleRegisterPushCallback = callback;
    [self registerPush];
    return;
}

- (NSDictionary *)getCachedMsg:(NSDictionary *) param {
    if (!self.hasGotCache) {
        self.hasGotCache = YES;
        
        return @{
            @"msgs": self.cachedMsgs
        };
    } else {
        return @{
            @"msgs": @[]
        };
    }
}

- (NSDictionary *)setBadge:(NSDictionary *) param {
    NSNumber *num = [param objectForKey:@"number"];
    [[UIApplication sharedApplication] setApplicationIconBadgeNumber:[num intValue]];
    BOOL success = [JPUSHService setBadge:[num intValue]];
    
    return @{
        @"jiguangSuccess": @(success),
    };
}

- (void)myAsyncFunc:(NSDictionary *)param withCallback:(WeAppNativePluginCallback)callback {
    NSLog(@"myAsyncFunc %@", param);
    
    callback(@{ @"a": @"1", @"b": @[@1, @2], @"c": @3 });
}

#pragma mark - core funs

- (BOOL)registerPush {
    NSLog(@"janzenplugin Start real register Push");
    NSBundle *frameworkBundle = [NSBundle bundleForClass:[MyPlugin class]];
    NSURL *url = [frameworkBundle URLForResource:@"MiniPlugin" withExtension:@"bundle"];
    if (url) {
        NSBundle *resourceBundle = [NSBundle bundleWithURL:url];
        NSString *jsonFilePath = [resourceBundle pathForResource: [self pluginId] ofType:@"json"];
        if (jsonFilePath) {
            NSData *jsonData = [NSData dataWithContentsOfFile:jsonFilePath];
            if (jsonData) {
                // 读取文件内容
                NSError *error;
                // 将JSON数据转换为NSDictionary
                NSDictionary *jsonDict = [NSJSONSerialization JSONObjectWithData:jsonData options:kNilOptions error:&error];
                
                // 检查转换是否成功
                if (!jsonDict) {
                    NSLog(@"Error parsing JSON: %@", error);
                    return NO;
                }
                
                NSString *appKey = jsonDict[@"appKey"];
                NSString *channel = jsonDict[@"channel"];
                NSNumber *apsForProduction = jsonDict[@"apsForProduction"];
                
                if (appKey && channel && apsForProduction) {
                    //Required
                    //notice: 3.0.0 及以后版本注册可以这样写，也可以继续用之前的注册方式
                    JPUSHRegisterEntity * entity = [[JPUSHRegisterEntity alloc] init];
                    entity.types = JPAuthorizationOptionAlert|JPAuthorizationOptionBadge|JPAuthorizationOptionSound|JPAuthorizationOptionProvidesAppNotificationSettings;
                    if ([[UIDevice currentDevice].systemVersion floatValue] >= 8.0) {
                        // 可以添加自定义 categories
                        // NSSet<UNNotificationCategory *> *categories for iOS10 or later
                        // NSSet<UIUserNotificationCategory *> *categories for iOS8 and iOS9
                    }
                    
                    [JPUSHService registerForRemoteNotificationConfig:entity delegate:self];
                    NSLog(@"janzenplugin registerForRemoteNotificationConfig done");
                    
                    // Required
                    // init Push
                    // notice: 2.1.5 版本的 SDK 新增的注册方法，改成可上报 IDFA，如果没有使用 IDFA 直接传 nil
                    //初始化极光推送服务，调用了本 API 后，开启 JPush 推送服务，将会开始收集上报 SDK 业务功能所必要的用户个人信息
                    [JPUSHService setupWithOption:self.launchOptions appKey:appKey
                                          channel:channel
                                 apsForProduction:[apsForProduction boolValue]
                            advertisingIdentifier:nil];
                    
                    return YES;
                }
            }
        }
    }
    
    return NO;
}

- (void)setGetTokenResult:(NSString *)result {
    [[NSUserDefaults standardUserDefaults] setObject:result forKey:@"__XGPUSH_START_RESULT"];
    [[NSUserDefaults standardUserDefaults] synchronize];
}

- (BOOL)getGetTokenResult {
    NSString* result = [[NSUserDefaults standardUserDefaults] objectForKey:@"__XGPUSH_START_RESULT"];
    return [result isEqual:@"success"];
}

- (void)sendMsg:(NSDictionary *)msg {
    NSLog(@"janzenplugin sendMsg");
    if (self.hasGotCache == YES) {
        NSLog(@"janzenplugin after Got cache, and sendMsg directly");
        [self sendMiniPluginEvent:@{
            @"ios": msg
        }];
    } else {
        NSLog(@"janzenplugin before Got cache , and cache");
        [self.cachedMsgs addObject:@{
            @"ios": msg
        }];
    }
}

#pragma mark - 极光回调事件

// iOS 12 Support
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center openSettingsForNotification:(UNNotification *)notification{
    NSLog(@"janzenplugin openSettingsForNotification");
    
    NSDictionary * userInfo = notification.request.content.userInfo;
    if (notification && [notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
        // 从通知界面直接进入应用
        [self sendMsg:@{
            @"type": @"openSettingsForNotification",
            @"data": userInfo,
            @"msg": @"从通知界面直接进入应用"
        }];
    } else {
        //从通知设置界面进入应用
        [self sendMsg:@{
            @"type": @"openSettingsForNotification",
            @"data": userInfo,
            @"msg": @"从通知设置界面进入应用"
        }];
    }
}

// iOS 10 Support
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(NSInteger))completionHandler {
    NSLog(@"janzenplugin willPresentNotification withCompletionHandler");
    // Required
    NSDictionary * userInfo = notification.request.content.userInfo;
    if([notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
        [JPUSHService handleRemoteNotification:userInfo];
    }
    completionHandler(UNNotificationPresentationOptionAlert); // 需要执行这个方法，选择是否提醒用户，有 Badge、Sound、Alert 三种类型可以选择设置
    
    [self sendMsg:@{
        @"type": @"willPresentNotification",
        @"data": userInfo
    }];
}

// iOS 10 Support
- (void)jpushNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)())completionHandler {
    NSLog(@"janzenplugin didReceiveNotificationResponse withCompletionHandler");
    
    // Required
    NSDictionary * userInfo = response.notification.request.content.userInfo;
    if([response.notification.request.trigger isKindOfClass:[UNPushNotificationTrigger class]]) {
        [JPUSHService handleRemoteNotification:userInfo];
    }
    completionHandler();    // 系统要求执行这个方法
    
    [self sendMsg:@{
        @"type": @"didReceiveNotificationResponse",
        @"data": userInfo
    }];
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo fetchCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler {
    NSLog(@"janzenplugin didReceiveRemoteNotification with fetchCompletionHandler");
    // Required, iOS 7 Support
    [JPUSHService handleRemoteNotification:userInfo];
    completionHandler(UIBackgroundFetchResultNewData);
    
    [self sendMsg:@{
        @"type": @"didReceiveRemoteNotification",
        @"data": userInfo
    }];
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)userInfo {
    NSLog(@"janzenplugin didReceiveRemoteNotification");
    // Required, For systems with less than or equal to iOS 6
    [JPUSHService handleRemoteNotification:userInfo];
    [self sendMsg:@{
        @"type": @"didReceiveRemoteNotification",
        @"data": userInfo
    }];
    
}

#pragma mark - 插件生命周期

// 插件初始化方法，在注册插件后会被自动调用
- (void)initPlugin {
    NSLog(@"initPlugin");
    self.cachedMsgs = [[NSMutableArray alloc] init];
    self.hasGotCache = NO;

    [self registerAppDelegateMethod:@selector(application:openURL:options:)];
    [self registerAppDelegateMethod:@selector(application:continueUserActivity:restorationHandler:)];
    [self registerAppDelegateMethod:@selector(application:didFailToRegisterForRemoteNotificationsWithError:)];
    [self registerAppDelegateMethod:@selector(application:didRegisterForRemoteNotificationsWithDeviceToken:)];
    [self registerAppDelegateMethod:@selector(application:didFinishLaunchingWithOptions:)];
}

- (void)application:(UIApplication *)app openURL:(NSURL *)url options:(NSDictionary<UIApplicationOpenURLOptionsKey, id> *)options {
    NSLog(@"url scheme");
}

- (void)application:(UIApplication *)application continueUserActivity:(NSUserActivity *)userActivity restorationHandler:(void (^)(NSArray<id<UIUserActivityRestoring>> *__nullable restorableObjects))restorationHandler {
    NSLog(@"universal link");
}

-(BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    NSLog(@"janzenplugin didFinishLaunchingWithOptions");
    NSDictionary *remoteNotification = [launchOptions objectForKey: UIApplicationLaunchOptionsRemoteNotificationKey];
    if (remoteNotification) {
        NSLog(@"janzenplugin didFinishLaunchingWithOptions with remoteNotification");
//        [self sendMsg:@{
//            @"type": @"didFinishLaunchingWithOptions",
//            @"data":remoteNotification,
//        }];
    } else {
        NSLog(@"janzenplugin didFinishLaunchingWithOptions without remoteNotification");
    }
    
    self.launchOptions = launchOptions;
    
    // 如果是App之前就已经注册过了，
    if ([self getGetTokenResult]) {
        [self registerPush];
    }
    return NO;
}

- (NSString *)stringWithDeviceToken:(NSData *)deviceToken {
    const char *data = (const char *)[deviceToken bytes];
    NSMutableString *token = [NSMutableString string];

    for (NSUInteger i = 0; i < [deviceToken length]; i++) {
        [token appendFormat:@"%02.2hhX", data[i]];
    }

    return [token copy];
}

- (void)application:(UIApplication *)application
didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken {
    NSString *token = [self stringWithDeviceToken: deviceToken];

    [self setGetTokenResult:@"success"];
    /// Required - 注册 DeviceToken
    [JPUSHService registerDeviceToken:deviceToken];
    
    if (self.handleRegisterPushCallback) {
        self.handleRegisterPushCallback(@{
            @"success": @YES,
            @"token": token
        });
        
        self.handleRegisterPushCallback = nil;
    }
}

- (void)application:(UIApplication *)application didFailToRegisterForRemoteNotificationsWithError:(NSError *)error {
    [self setGetTokenResult:@"fail"];
    
    self.handleRegisterPushCallback(@{
        @"success": @NO,
        @"msg": error.description
    });
    
    self.handleRegisterPushCallback = nil;
    //Optional
    NSLog(@"did Fail To Register For Remote Notifications With Error: %@", error);
}


@end
