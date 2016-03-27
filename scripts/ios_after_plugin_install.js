module.exports = function(context) {

    var cordova_util = context.requireCordovaModule("cordova-lib/src/cordova/util"),
        ConfigParser = context.requireCordovaModule('cordova-common').ConfigParser,
        fs = context.requireCordovaModule('fs'),
        path = context.requireCordovaModule('path'),
        exec = require('child_process').exec,
        xcode = require('xcode');

    var xml = cordova_util.projectConfig(context.opts.projectRoot);
    var cfg = new ConfigParser(xml);
    var packageName = cfg.packageName();
    // var androidJSON = JSON.parse(fs.readFileSync(context.opts.projectRoot + '/plugins/android.json'));
    var appName = cfg.name();

    var wxpayPluginPath = context.opts.projectRoot + path.sep+'plugins/nova.cordova.plugins.wxpay/src/ios';
    var wxpayProjectRootPath = context.opts.projectRoot +path.sep+ 'platforms/ios';

    projectPath = wxpayProjectRootPath + path.sep +  appName + '.xcodeproj/project.pbxproj';
    var myProj = xcode.project(projectPath);

    myProj.parse(function (err) {
        var searchPath = '"$(PROJECT_DIR)/$(PROJECT_NAME)/Plugins/nova.cordova.plugins.wxpay"';
        myProj.addToHeaderSearchPaths(searchPath);
        myProj.addToLibrarySearchPaths(searchPath);
        fs.writeFileSync(projectPath, myProj.writeSync());
    });

    var iosJSON = JSON.parse(fs.readFileSync(context.opts.projectRoot + '/plugins/ios.json'));
    var wxpayFolderPath = wxpayProjectRootPath +path.sep + appName + '/Plugins/nova.cordova.plugins.wxpay';
    var wxpayFile = wxpayFolderPath+'/payRequsestHandler.h';
    fs.readFile(wxpayFile, 'utf8', function(err, data) {
        if (err) throw err;
        var result = data.replace(/\$APPID/g, iosJSON.installed_plugins["nova.cordova.plugins.wxpay"].APPID);
        result = result.replace(/\$MCHID/g, iosJSON.installed_plugins["nova.cordova.plugins.wxpay"].MCHID);
        result = result.replace(/\$APIKEY/g, iosJSON.installed_plugins["nova.cordova.plugins.wxpay"].APIKEY);
        fs.exists(wxpayFolderPath, function(exists) {
            if (!exists) fs.mkdir(wxpayFolderPath);
            fs.writeFile(wxpayFile, result, 'utf8', function(err) {
                if (err) throw err;
            });
        });

    });

}
