
var CordovaPush = function () { };


/**
 * 注册推送服务
 * @param successCallback 成功回调
 * @param errorCallback 失败回调
 * @param options 参数
 */
CordovaPush.prototype.register = function (successCallback, errorCallback, options) {
    cordova.exec(successCallback, errorCallback, "CordovaPushPlugin", "register", [options]);
};



module.exports = new PushNotification();























































console.log("定义 window.cordova.plugins.huaweipush");

if (window.cordova == null) {
    window.cordova = {};
    window.cordova.plugins = {};
    window.cordova.plugins.huaweipush = {};
} else {
    if (window.cordova.plugins == null) {
        window.cordova.plugins = {};
    }
    window.cordova.plugins.huaweipush = {};
}

window.cordova.plugins.huaweipush.isAndroidDevice = function () {
    return device.platform == 'Android';
}
// 获取到token
window.cordova.plugins.huaweipush.tokenRegistered = function (token) {
    try {
        cordova.fireDocumentEvent('huaweipush.receiveRegisterResult', token);
    } catch (exception) {
        console.log('HuaweiPush:tokenRegistered ' + exception);
    }
}
// 透传消息
window.cordova.plugins.huaweipush.pushMsgReceived = function (msg) {
    try {
        //msg.extras = JSON.parse(msg.extras);
        cordova.fireDocumentEvent('huaweipush.pushMsgReceived', msg);
    } catch (exception) {
        console.log('HuaweiPush:pushMsgReceived ' + exception);
    }
}
//通知消息
window.cordova.plugins.huaweipush.notificationOpened = function (msg) {
    try {
        console.log(msg);
        //console.log(msg.extras);
        //msg.extras = JSON.parse(msg.extras);
        cordova.fireDocumentEvent('huaweipush.notificationOpened', msg);
    } catch (exception) {
        console.log('HuaweiPush:notificationOpened ' + exception);
    }
}

//输出调试日志
window.cordova.plugins.huaweipush.log = function (logStr) {
    var platform = device.platform;
    if (platform.toLowerCase() == "android") {
        try {
            cordova.fireDocumentEvent('huaweipush.log', logStr);
        } catch (exception) {
            console.log('HuaweiPush:tokenRegistered ' + exception);
        }
    }
}






