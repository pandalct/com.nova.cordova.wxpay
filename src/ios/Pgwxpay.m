//
//  pgwxpay.h
//  pgtest
//
//  Created by breadth on 14-7-31.
//
//

#import "Pgwxpay.h"
#import "payRequsestHandler.h"
#import "WXApi.h"
#import <QuartzCore/QuartzCore.h>


@implementation Pgwxpay

NSString *timeStamp;
NSString *nonceStr;
NSString *traceId;

#pragma mark - 主体流程
// 获取token


//检查是否安装了微信
- (void)iswx:(CDVInvokedUrlCommand *)command{
    
    BOOL iswx =  [WXApi isWXAppInstalled];
    CDVPluginResult* pluginResult = nil;
    
    if (iswx) {
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"1"];
        
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        
    } else {
        
        pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"0"];
        
        [self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
        
    }
    
}


- (void)wxpay:(CDVInvokedUrlCommand *)command{

   
   NSString* out_trade_no = [command.arguments objectAtIndex:0];
   NSString* url = [command.arguments objectAtIndex:1];
   NSString* bodtxt = [command.arguments objectAtIndex:2];
   NSString* total_fee = [command.arguments objectAtIndex:3];
 
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setObject:out_trade_no forKey:@"out_trade_no"];
    [userDefaults setObject:url forKey:@"url"];
    [userDefaults setObject:bodtxt forKey:@"bodtxt"];
    [userDefaults setObject:total_fee forKey:@"total_fee"];

    
    //创建支付签名对象
    payRequsestHandler *req = [[payRequsestHandler alloc] autorelease];
    //初始化支付签名对象
    [req init:APP_ID mch_id:MCH_ID];
    //设置密钥
    [req setKey:PARTNER_key];
    
 
    
    //获取到实际调起微信支付的参数后，在app端调起支付
    NSMutableDictionary *dict = [req sendPay];
    
    if(dict == nil){
        //错误提示
        NSString *debug = [req getDebugifo];
        
        [self alert:@"提示信息" msg:debug];
        
        NSLog(@"%@\n\n",debug);
    }else{
        NSLog(@"%@\n\n",[req getDebugifo]);
        //[self alert:@"确认" msg:@"下单成功，点击OK后调起支付！"];
        
        NSMutableString *stamp  = [dict objectForKey:@"timestamp"];
        
        //调起微信支付
        PayReq* req             = [[[PayReq alloc] init]autorelease];
        req.openID              = [dict objectForKey:@"appid"];
        req.partnerId           = [dict objectForKey:@"partnerid"];
        req.prepayId            = [dict objectForKey:@"prepayid"];
        req.nonceStr            = [dict objectForKey:@"noncestr"];
        req.timeStamp           = stamp.intValue;
        req.package             = [dict objectForKey:@"package"];
        req.sign                = [dict objectForKey:@"sign"];
        
        [WXApi sendReq:req];
    }



}

//客户端提示信息
- (void)alert:(NSString *)title msg:(NSString *)msg
{
    UIAlertView *alter = [[UIAlertView alloc] initWithTitle:title message:msg delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil];
    
    [alter show];
    [alter release];
}




  
@end
 
