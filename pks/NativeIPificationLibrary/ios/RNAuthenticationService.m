#import <Foundation/Foundation.h>

#if __has_include("RCTBridgeModule.h")
#import "RCTBridgeModule.h"
#else
#import <React/RCTBridgeModule.h>
#endif

@interface RCT_EXTERN_MODULE(RNAuthenticationService,NSObject)
RCT_EXTERN_METHOD(unregisterNetwork)
RCT_EXTERN_METHOD(setAuthorizationServiceConfiguration:(NSString *)name)
RCT_EXTERN_METHOD(doAuthorizationNoParam: (RCTResponseSenderBlock)callback)
RCT_EXTERN_METHOD(doAuthorization: (NSDictionary *)params cb:(RCTResponseSenderBlock)callback )
RCT_EXTERN_METHOD(
                  getConfigurationByName: (NSString *)name
                  resolve: (RCTPromiseResolveBlock)resolve
                  rejecter:(RCTPromiseRejectBlock)reject
                  )
@end
