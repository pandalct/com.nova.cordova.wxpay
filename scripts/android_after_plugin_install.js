module.exports = function(context) {

    var cordova_util = context.requireCordovaModule("cordova-lib/src/cordova/util"),
        ConfigParser = context.requireCordovaModule('cordova-common').ConfigParser,
        fs = context.requireCordovaModule('fs');

    var xml = cordova_util.projectConfig(context.opts.projectRoot);
    var cfg = new ConfigParser(xml);

    var packageName = cfg.packageName();
    var androidJSON = JSON.parse(fs.readFileSync(context.opts.projectRoot + '/plugins/android.json'));
    var wxpayPath = context.opts.projectRoot + '/platforms/android/src/com/nova/cordova/wxpay';
    var wxpayKeysPath = wxpayPath + '/Keys.java';

    fs.readFile(context.opts.projectRoot + '/plugins/com.nova.cordova.wxpay/src/android/com/nova/cordova/wxpay/Keyswx.java', 'utf8', function(err, data) {
        if (err) throw err;
        var result = data.replace(/\$APPID/g, androidJSON.installed_plugins["com.nova.cordova.wxpay"].APPID);
        result = result.replace(/\$MCHID/g, androidJSON.installed_plugins["com.nova.cordova.wxpay"].MCHID);
        result = result.replace(/\$APIKEY/g, androidJSON.installed_plugins["com.nova.cordova.wxpay"].APIKEY);
        fs.exists(wxpayPath, function(exists) {
            if (!exists) fs.mkdir(wxpayPath);
            fs.writeFile(wxpayKeysPath, result, 'utf8', function(err) {
                if (err) throw err;
            });
        });

    });

    var wxpayEntryPath = context.opts.projectRoot + '/platforms/android/src/' + packageName.replace(/./g, '/'); + '/wxapi';
    var wxpayEntryActivityPath = wxpayEntryPath + 'WXPayEntryActivity.java';
    fs.readFile(context.opts.projectRoot + '/plugins/com.nova.cordova.wxpay/src/android/WXPayEntryActivity.java', 'utf8', function(err, data) {
        if (err) throw err;
        var result = data.replace(/\$PACKAGENAME/g, packageName);
        fs.exists(wxpayEntryPath, function(exists) {
            if (!exists) fs.mkdir(wxpayEntryPath);
            fs.writeFile(wxpayEntryActivityPath, result, 'utf8', function(err) {
                if (err) throw err;
            });
        });

    });

}
