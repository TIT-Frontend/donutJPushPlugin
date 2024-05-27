//
//  WeAppNativePlugin.h
//  WeAppNativePlugin
//

#import <Foundation/Foundation.h>

#ifndef _WEAPPMODULENATIVEPLUGIN_
#define _WEAPPMODULENATIVEPLUGIN_

NS_ASSUME_NONNULL_BEGIN

typedef void (^WeAppNativePluginCallback)(id _Nullable ret);

@protocol WeAppNativePluginProtocol <NSObject>

#define WEAPP_DEFINE_PLUGIN_ID(plugin_id) \
- (NSString *)pluginId { \
    return @#plugin_id; \
}                                        \

#define WEAPP_EXPORT_PLUGIN_METHOD_SYNC(methodName, methodSelector) \
- (void)__weapp_plugin_method_sync__##methodName { \
    [self registerSyncMethod:@{ @"methodName": @#methodName, @"selectorName": NSStringFromSelector(methodSelector) }]; \
}


#define WEAPP_EXPORT_PLUGIN_METHOD_ASYNC(methodName, methodSelector) \
- (void)__weapp_plugin_method_async__##methodName { \
    [self registerAsyncMethod:@{ @"methodName": @#methodName, @"selectorName": NSStringFromSelector(methodSelector) }]; \
}


@required
- (NSString *) pluginId;

@optional
- (void)initPlugin;

@end

@interface WeAppNativePlugin : NSObject

+ (BOOL)registerPluginAndInit:(WeAppNativePlugin *)pluginClassInstance;
- (NSString *)getPluginId;
- (void)registerAsyncMethod:(NSDictionary *)name;
- (void)registerSyncMethod:(NSDictionary *)name;
- (void)registerAppDelegateMethod:(SEL)appMethod;
- (void)sendMiniPluginEvent:(NSDictionary *)param;

@end

NS_ASSUME_NONNULL_END

#endif
