package com.phonegap.weixin;

/**
 * 这个插件是基于微信支付实现的的phonegap插件.
 * 该插件封装了微信的快捷支付模块.
 * 其中的一些业务数据还需要再次根据需求来处理.
 * .
 *
 * Copyright (c) Matt ZHENGJUN 2014-7-25 
 *   
 */
 
import java.io.StringReader; 
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

    


import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;   
import org.xmlpull.v1.XmlPullParser;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class Pgwxpay extends CordovaPlugin  {
	
	public static final String TAG = "weixin-sdk";  
	public static CallbackContext callbackContext;
	 
	PayReq req;	  
	Map<String,String> resultunifiedorder;
	StringBuffer sb;
	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) {
		  this.callbackContext = callbackContext;
		 if (action.equals("wxpay")) {
			 
			
				final IWXAPI msgApi = WXAPIFactory.createWXAPI(cordova.getActivity(), null);
				req = new PayReq();
				sb=new StringBuffer();
				
				msgApi.registerApp(Keyswx.APP_ID);  
			   
	           JSONObject cityad = args.optJSONObject(0);
	           String out_trade_no = cityad.optString("out_trade_no");
	           String url = cityad.optString("url");
	           String body = cityad.optString("bodtxt");
	           String total_fee = cityad.optString("total_fee");
	           
	         //服务器异步通知页面路径,需要自己定义  参数 notify_url，如果商户没设定，则不会进行该操作
//	           注意由客户端返回的支付结果不能作为最终支付的可信结果，应以服务器端的支付结果通知为准。
	           
//	           String url = "http://dev.breadth.com.cn/zhifu/alipay/notify_url.php";
	           Toast.makeText(cordova.getActivity(), "正在调起微信支付", Toast.LENGTH_SHORT).show();
	           final String entryres =  genProductArgs(out_trade_no,url,body,total_fee); //订单构造数据
	           Runnable payRunnable = new Runnable() {

					@Override
					public void run() {   
				         
				        final Map<String, String> result2 = doInBackground(entryres);
				        
						req.appId = Keyswx.APP_ID;
						req.partnerId = Keyswx.MCH_ID;
						req.prepayId = result2.get("prepay_id");
						req.packageValue = "prepay_id="+result2.get("prepay_id");
						req.nonceStr = genNonceStr();
						req.timeStamp = String.valueOf(genTimeStamp()); 

						List<NameValuePair> signParams = new LinkedList<NameValuePair>();
						signParams.add(new BasicNameValuePair("appid", req.appId));
						signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
						signParams.add(new BasicNameValuePair("package", req.packageValue));
						signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
						signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
						signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

						req.sign = genAppSign(signParams);
						sb.append("sign\n"+req.sign+"\n\n"); 

						Log.e("orion", signParams.toString()); 
					    
						msgApi.sendReq(req);
					}
					
				};

				Thread payThread = new Thread(payRunnable);
				payThread.start(); 	           
	            return true;     
	            
		 
	        }else {
	        	
	        	callbackContext.error("Invalid Action");
	            return false;
   }
		 
		 
		 
}
	 
	 

private String genProductArgs(String out_trade_no,String url,String body, String price) {
	StringBuffer xml = new StringBuffer(); 
	try {
		String	nonceStr = genNonceStr();

		xml.append("</xml>");
       List<NameValuePair> packageParams = new LinkedList<NameValuePair>();
		packageParams.add(new BasicNameValuePair("appid", Keyswx.APP_ID));
		packageParams.add(new BasicNameValuePair("body", body));
		packageParams.add(new BasicNameValuePair("mch_id", Keyswx.MCH_ID));
		packageParams.add(new BasicNameValuePair("nonce_str", nonceStr));
		packageParams.add(new BasicNameValuePair("notify_url", url));
		packageParams.add(new BasicNameValuePair("out_trade_no",out_trade_no));
		packageParams.add(new BasicNameValuePair("spbill_create_ip","127.0.0.1"));
		packageParams.add(new BasicNameValuePair("total_fee", price));
		packageParams.add(new BasicNameValuePair("trade_type", "APP"));  

		String sign = genPackageSign(packageParams);
		packageParams.add(new BasicNameValuePair("sign", sign));


	   String xmlstring =toXml(packageParams);

		return xmlstring;

	} catch (Exception e) {
		Log.e(TAG, "genProductArgs fail, ex = " + e.getMessage());
		return null;
	}
	

}


/**
生成签名
*/

private String genPackageSign(List<NameValuePair> params) {
	StringBuilder sb = new StringBuilder();
	
	for (int i = 0; i < params.size(); i++) {
		sb.append(params.get(i).getName());
		sb.append('=');
		sb.append(params.get(i).getValue());
		sb.append('&');
	}
	sb.append("key=");
	sb.append(Keyswx.API_KEY);
	

	String packageSign = MD5.getMessageDigest(sb.toString().getBytes()).toUpperCase();
	Log.e("orion",packageSign);
	return packageSign;
}


private String toXml(List<NameValuePair> params) {
	StringBuilder sb = new StringBuilder();
	 sb.append("<xml version='1.0' encoding='UTF-8' standalone='yes'>"); 
	for (int i = 0; i < params.size(); i++) {
		sb.append("<"+params.get(i).getName()+">");


		sb.append(params.get(i).getValue());
		sb.append("</"+params.get(i).getName()+">");
	}
	sb.append("</xml>");

	Log.e("orion",sb.toString());
	try {  
        return new String(sb.toString().getBytes(), "ISO8859-1");  
    } catch (Exception e) {  
        e.printStackTrace();  
    }  
    return "";
	 
}



private String genAppSign(List<NameValuePair> params) {
	StringBuilder sb = new StringBuilder();

	for (int i = 0; i < params.size(); i++) {
		sb.append(params.get(i).getName());
		sb.append('=');
		sb.append(params.get(i).getValue());
		sb.append('&');
	}
	sb.append("key=");
	sb.append(Keyswx.API_KEY);

    this.sb.append("sign str\n"+sb.toString()+"\n\n");
	String appSign = MD5.getMessageDigest(sb.toString().getBytes());
	Log.e("orion",appSign);
	return appSign;
}

 
	
	private String genNonceStr() {
		Random random = new Random();
		return MD5.getMessageDigest(String.valueOf(random.nextInt(10000)).getBytes());
	}
	
	private long genTimeStamp() {
		return System.currentTimeMillis() / 1000;
	}
	
  
	protected Map<String,String> doInBackground(String entity1) {

		String url = String.format("https://api.mch.weixin.qq.com/pay/unifiedorder");
		String entity = entity1;

		Log.e("orion",entity);

		byte[] buf = Util.httpPost(url, entity);

		String content = new String(buf);
		Log.e("orion", content);
		Map<String,String> xml=decodeXml(content);

		return xml;
	}
	
	
	
	public Map<String,String> decodeXml(String content) {

		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {

				String nodeName=parser.getName();
				switch (event) {
					case XmlPullParser.START_DOCUMENT:

						break;
					case XmlPullParser.START_TAG:

						if("xml".equals(nodeName)==false){
							//实例化student对象
							xml.put(nodeName,parser.nextText());
						}
						break;
					case XmlPullParser.END_TAG:
						break;
				}
				event = parser.next();
			}

			return xml;
		} catch (Exception e) {
			Log.e("orion",e.toString());
		}
		return null;

	} 
	
	
	
		 
}
	
  
 
	 
	 
 