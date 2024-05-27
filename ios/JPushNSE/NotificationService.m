//
//  NotificationService.m
//  JPushNSE
//
//  Created by 张晨 on 2024/4/22.
//

#import "NotificationService.h"
#import "JPushNotificationExtensionService.h"

@interface NotificationService ()

@property (nonatomic, strong) void (^contentHandler)(UNNotificationContent *contentToDeliver);
@property (nonatomic, strong) UNMutableNotificationContent *bestAttemptContent;

@end

@implementation NotificationService

- (void)didReceiveNotificationRequest:(UNNotificationRequest *)request withContentHandler:(void (^)(UNNotificationContent * _Nonnull))contentHandler {
    self.contentHandler = contentHandler;
    self.bestAttemptContent = [request.content mutableCopy];
    
    NSString* appKey = [self getAppKey];
    
    if (!appKey) {
        // Modify the notification content here...
//        self.bestAttemptContent.title = [NSString stringWithFormat:@"%@ [no appKey janzen NSE modified!!]", self.bestAttemptContent.title];
        NSString *logMsg = [NSString stringWithFormat:@"%@ [no appKey, NSE can be modified!!]", self.bestAttemptContent.title];
        NSLog(@"%@", logMsg);

        contentHandler(self.bestAttemptContent);
        return;
    } else {
        // Modify the notification content here...
//        self.bestAttemptContent.title = [NSString stringWithFormat:@"%@ [has appKey janzen NSE modified!!]", self.bestAttemptContent.title];
        NSString *logMsg = [NSString stringWithFormat:@"%@ [has appKey, NSE cant be  modified!!]", self.bestAttemptContent.title];
        NSLog(@"%@", logMsg);
        
        [JPushNotificationExtensionService jpushSetAppkey:appKey];

        [JPushNotificationExtensionService jpushReceiveNotificationRequest:request with:^ {
          NSLog(@"apns upload success");
          contentHandler(self.bestAttemptContent);
        }];
        
    }
}

- (NSString *)pluginId {
    return @"wxca742ba6781a54fd";
}

- (nullable NSString *) getAppKey {
    NSBundle *frameworkBundle = [NSBundle bundleForClass:[NotificationService class]];
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
                    return nil;
                }
                
                NSString *appKey = jsonDict[@"appKey"];
                
                if (appKey) {
                    return appKey;
                }
            }
        }
    }
    
    return nil;
}

- (void)serviceExtensionTimeWillExpire {
    // Called just before the extension will be terminated by the system.
    // Use this as an opportunity to deliver your "best attempt" at modified content, otherwise the original push payload will be used.
    self.contentHandler(self.bestAttemptContent);
}

@end
