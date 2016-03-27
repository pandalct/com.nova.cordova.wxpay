cordova.define("cordova/plugins/Pgwxpay", 
  function(require, exports, module) {
    var exec = require("cordova/exec");
    var Pgwxpay = function() {};
     //----------------------------//支付---------------------------------------
      Pgwxpay.prototype.wxpay = function(out_trade_no,url,bodtxt,total_fee,successCallback, errorCallback) {
        if (errorCallback == null) { errorCallback = function() {}}
    
        if (typeof errorCallback != "function")  {
            console.log("pgbaidumap.scan failure: failure parameter not a function");
            return
        }
    
        if (typeof successCallback != "function") {
            console.log("pgbaidumap.scan failure: success callback parameter must be a function");
            return
        }
    
        exec(successCallback, errorCallback, 'Pgwxpay', 'wxpay', [out_trade_no,url,bodtxt,total_fee]);
    };
      
        //----------------------------j检查是否安装微信---------------------------------------
     Pgwxpay.prototype.iswx = function(successCallback, errorCallback) {
        if (errorCallback == null) { errorCallback = function() {}}
    
        if (typeof errorCallback != "function")  {
            console.log("pgbaidumap.scan failure: failure parameter not a function");
            return
        }
    
        if (typeof successCallback != "function") {
            console.log("pgbaidumap.scan failure: success callback parameter must be a function");
            return
        }
    
        exec(successCallback, errorCallback, 'Pgwxpay', 'iswx', []);
    };
    
    var Pgwxpay = new Pgwxpay();
    module.exports = Pgwxpay;

});



  
if(!window.plugins) {
    window.plugins = {};
}
if (!window.plugins.Pgwxpay) {
    window.plugins.Pgwxpay = cordova.require("cordova/plugins/Pgwxpay");
}

 