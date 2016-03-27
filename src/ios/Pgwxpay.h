//
//  pgwxpay.h
//  pgtest
//
//  Created by breadth on 14-7-31.
//
//

#import <Foundation/Foundation.h>
#import <Cordova/CDVPlugin.h>
#import <UIKit/UIKit.h>
#import "WXApi.h"
#import <Cordova/CDVViewController.h>

@interface Pgwxpay : CDVPlugin<WXApiDelegate>
{
   enum WXScene _scene;
}
  
-(void)wxpay:(CDVInvokedUrlCommand*)command;
-(void)iswx:(CDVInvokedUrlCommand*)command;


@end





 