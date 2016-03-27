

#import <Foundation/Foundation.h>
#import "WXUtil.h"
#import "ApiXml.h"

// 账号帐户资料
//更改商户把相关参数后可测试

#define APP_ID          @"wx29e2d22181a6618d"               //APPID
 
//商户号，填写商户对应参数
#define MCH_ID          @"1233926702"
//商户API密钥，填写相应参数
#define PARTNER_key      @"1qazxsw22fr45tgbnhy67ujmki89"



@interface payRequsestHandler : NSObject{
	//预支付网关url地址
    NSString *payUrl;

    //lash_errcode;
    long     last_errcode;
	//debug信息
    NSMutableString *debugInfo;
    NSString *appid,*mchid,*spkey;
}
//初始化函数
-(BOOL) init:(NSString *)app_id mch_id:(NSString *)mch_id;
-(NSString *) getDebugifo;
-(long) getLasterrCode;
//设置商户密钥
-(void) setKey:(NSString *)key;
//创建package签名
-(NSString*) createMd5Sign:(NSMutableDictionary*)dict;
//获取package带参数的签名包
-(NSString *)genPackage:(NSMutableDictionary*)packageParams;
//提交预支付
-(NSString *)sendPrepay:(NSMutableDictionary *)prePayParams;
//签名实例测试
- ( NSMutableDictionary *)sendPay;

@end