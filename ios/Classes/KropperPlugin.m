#import "KropperPlugin.h"

@implementation KropperPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  FlutterMethodChannel* channel = [FlutterMethodChannel
      methodChannelWithName:@"kropper"
            binaryMessenger:[registrar messenger]];
  KropperPlugin* instance = [[KropperPlugin alloc] init];
  [registrar addMethodCallDelegate:instance channel:channel];
}

- (void)handleMethodCall:(FlutterMethodCall*)call result:(FlutterResult)result {
  if ([@"getPlatformVersion" isEqualToString:call.method]) {
      result([@"iOS " stringByAppendingString:[[UIDevice currentDevice] systemVersion]]);
  } else if([@"cropImage" isEqualToString:call.method]) {
      NSString *sourcePath = [[call arguments] objectForKey:@"imagePath"];
      NSString *imagePath = @"/";
      result(imagePath);
  } else {
      result(FlutterMethodNotImplemented);
  }
}

@end
