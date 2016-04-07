Hello nova.cordova.plugins.alipay     



phonegap plugin add /Users/terry/Projects/nova.cordova.plugins.wxpay --variable APPID={APPID} --variable MCHID={MCHID} --variable APIKEY={APIKEY}


iOS


AppDelegate.h 中需要修改：

添加引入#import "WXApi.h" 

修改 @interface AppDelegate : NSObject <UIApplicationDelegate>{}

为：

@interface AppDelegate : NSObject <UIApplicationDelegate,WXApiDelegate>{}




AppDelegate.m中修改： 

添加引入#import "payRequsestHandler.h"

再didFinishLaunchingWithOptions 方法里面添加：

// 向微信终端注册appID
    [WXApi registerApp:APP_ID withDescription:@"weixin pay"];
 
///end


再以下方法里面添加：
 - (BOOL)application:(UIApplication*)application openURL:(NSURL*)url sourceApplication:(NSString*)sourceApplication annotation:(id)annotation:

【再openURL 方法里面添加：】

//支付宝支付完成后结果end
     return [WXApi handleOpenURL:url delegate:self];
      

添加以下代码：
（如果handleOpenURL存在则只需要添加：return  [WXApi handleOpenURL:url delegate:self]; 在方法的里面。）

- (BOOL)application:(UIApplication *)application handleOpenURL:(NSURL *)url
{
    return  [WXApi handleOpenURL:url delegate:self];
}



再随后位置上添加以下方法：

//微信app独立客户端回调函数

- (void)onResp:(BaseResp *)resp
{
    if ([resp isKindOfClass:[PayResp class]])
    {
        PayResp *response = (PayResp *)resp;
        [self.viewController.webView stringByEvaluatingJavaScriptFromString:[NSString stringWithFormat:@"wxresults('%d')",response.errCode]];
        
    }
}
