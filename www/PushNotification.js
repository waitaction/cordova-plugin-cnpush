
var PushNotification = function () { };

/** 
 * 注册接收推送
 * Call this to register for push notifications. 
 * Content of [options] depends on whether we are working with APNS (iOS) or GCM (Android)
*/
PushNotification.prototype.register = function (successCallback, errorCallback, options) {
    if (errorCallback == null) { errorCallback = function () { } }

    if (typeof errorCallback != "function") {
        console.log("PushNotification.register failure: failure parameter not a function");
        return
    }

    if (typeof successCallback != "function") {
        console.log("PushNotification.register failure: success callback parameter must be a function");
        return
    }
    var platform = device.platform;
    if (platform.toLowerCase() == "android") {
        if (device.manufacturer.toLowerCase().indexOf("huawei") >= 0) {
            document.addEventListener('huaweipush.log', function (ev) {
                console.log(ev);
            });
            document.addEventListener('huaweipush.receiveRegisterResult', function (result) {
                console.log(result);
                if (result.token != null) {
                    successCallback(result.token);
                }
            });
            document.addEventListener('huaweipush.notificationOpened', window[options.ecb]);
            cordova.exec(successCallback, errorCallback, "PushPlugin", "init", [options]);
        } else {
            try {
                function getRegistrationID() {
                    window["plugins"].jPushPlugin.getRegistrationID((token) => {
                        try {
                            console.log(token);
                            if (token.length == 0) {
                                window.setTimeout(getRegistrationID, 120000);
                            } else {
                                successCallback(token);
                            }
                        } catch (exception) {
                            console.log(exception);
                        }
                    });
                }
                window["plugins"].jPushPlugin.init();
                document.addEventListener("jpush.openNotification", window[options.ecb], false);
                window.setTimeout(getRegistrationID, 1000);
                window["plugins"].jPushPlugin.setDebugMode(true);
                window["plugins"].jPushPlugin.setStatisticsOpen(true);
            } catch (exception) {
                console.log("初始化极光推送失败");
                console.log(exception);
            }
        }

    } else {
        cordova.exec(successCallback, errorCallback, "PushPlugin", "register", [options]);
    }


};

/**
 * 取消注册接收推送
 * Call this to unregister for push notifications
 */
PushNotification.prototype.unregister = function (successCallback, errorCallback, options) {
    if (errorCallback == null) { errorCallback = function () { } }

    if (typeof errorCallback != "function") {
        console.log("PushNotification.unregister failure: failure parameter not a function");
        return
    }

    if (typeof successCallback != "function") {
        console.log("PushNotification.unregister failure: success callback parameter must be a function");
        return
    }

    var platform = device.platform;
    if (platform.toLowerCase() == "android") {
        if (device.manufacturer.toLowerCase().indexOf("huawei") >= 0) {
            cordova.exec(successCallback, errorCallback, "PushPlugin", "stop", []);
        } else {

        }

    } else {
        cordova.exec(successCallback, errorCallback, "PushPlugin", "unregister", [options]);
    }
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
        Î
        try {
            cordova.fireDocumentEvent('huaweipush.log', logStr);
        } catch (exception) {
            console.log('HuaweiPush:tokenRegistered ' + exception);
        }
    }
}






