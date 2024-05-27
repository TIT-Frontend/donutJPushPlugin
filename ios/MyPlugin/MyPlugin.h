//
//  myPlugin.h
//
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface MyPlugin: WeAppNativePlugin

@property (nullable, atomic, strong) NSDictionary *launchOptions;

@property (nullable, atomic, strong) WeAppNativePluginCallback handleRegisterPushCallback;

@property (nullable, atomic, strong) NSMutableArray *cachedMsgs;

// 是否获取过cache 
@property (atomic, assign) BOOL hasGotCache;

@end

NS_ASSUME_NONNULL_END

