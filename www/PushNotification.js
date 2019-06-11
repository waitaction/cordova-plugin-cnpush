

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
        document.addEventListener('huaweipush.log', function (ev) {
            console.log(ev);
        });
        document.addEventListener('huaweipush.receiveRegisterResult', successCallback);
        document.addEventListener('huaweipush.notificationOpened', options.ecb);
        cordova.exec(successCallback, errorCallback, "PushPlugin", "init", [options]);
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
        cordova.exec(successCallback, errorCallback, "PushPlugin", "stop", []);
    } else {
        cordova.exec(successCallback, errorCallback, "PushPlugin", "unregister", [options]);
    }
};


// /**
//  * 显示通知提示
//  * Call this if you want to show toast notification on WP8
//  */
// PushNotification.prototype.showToastNotification = function (successCallback, errorCallback, options) {
//     if (errorCallback == null) { errorCallback = function () { } }

//     if (typeof errorCallback != "function") {
//         console.log("PushNotification.register failure: failure parameter not a function");
//         return
//     }

//     cordova.exec(successCallback, errorCallback, "PushPlugin", "showToastNotification", [options]);
// }

// /**
//  * Call this to set the application icon badge
//  */
// PushNotification.prototype.setApplicationIconBadgeNumber = function (successCallback, errorCallback, badge) {
//     if (errorCallback == null) { errorCallback = function () { } }

//     if (typeof errorCallback != "function") {
//         console.log("PushNotification.setApplicationIconBadgeNumber failure: failure parameter not a function");
//         return
//     }

//     if (typeof successCallback != "function") {
//         console.log("PushNotification.setApplicationIconBadgeNumber failure: success callback parameter must be a function");
//         return
//     }

//     cordova.exec(successCallback, errorCallback, "PushPlugin", "setApplicationIconBadgeNumber", [{ badge: badge }]);
// };

module.exports = new PushNotification();

















// var HuaweiPush = function () { }
// HuaweiPush.prototype.isAndroidDevice = function () {
//     return device.platform == 'Android';
// }
// // 获取到token
// HuaweiPush.prototype.tokenRegistered = function (token) {
//     try {
//         this.receiveRegisterResult = token;
//         cordova.fireDocumentEvent('huaweipush.receiveRegisterResult', this.receiveRegisterResult);
//     } catch (exception) {
//         console.log('HuaweiPush:tokenRegistered ' + exception);
//     }
// }
// // 透传消息
// HuaweiPush.prototype.pushMsgReceived = function (msg) {
//     try {
//         msg.extras = JSON.parse(msg.extras);
//         this.receiveRegisterResult = msg;
//         cordova.fireDocumentEvent('huaweipush.pushMsgReceived', this.receiveRegisterResult);
//     } catch (exception) {
//         console.log('HuaweiPush:pushMsgReceived ' + exception);
//     }
// }
// //通知消息
// HuaweiPush.prototype.notificationOpened = function (msg) {
//     try {
//         console.log(msg);
//         console.log(msg.extras);
//         msg.extras = JSON.parse(msg.extras);
//         this.receiveRegisterResult = msg;
//         cordova.fireDocumentEvent('huaweipush.notificationOpened', this.receiveRegisterResult);
//     } catch (exception) {
//         console.log('HuaweiPush:notificationOpened ' + exception);
//     }
// }
// //初始
// HuaweiPush.prototype.init = function (success, error) {
//     if (this.isAndroidDevice()) {
//         exec(success, error, "CordovaHuaweiHMS", "init", []);
//     }
// }
// //停止hms服务
// HuaweiPush.prototype.stop = function (success, error) {
//     if (this.isAndroidDevice()) {
//         exec(success, error, "CordovaHuaweiHMS", "stop", []);
//     }
// }

// //连接到hms服务
// HuaweiPush.prototype.connect = function (success, error) {
//     if (this.isAndroidDevice()) {
//         exec(success, error, "CordovaHuaweiHMS", "connect", []);
//     }
// }

// //是否已连接
// HuaweiPush.prototype.isConnected = function (success, error) {
//     if (this.isAndroidDevice()) {
//         exec(success, error, "CordovaHuaweiHMS", "isConnected", []);

//     }
// }

// //付款 productName:商品名 productDesc:商品描述 amount:金额 requestId:订单号 merchantName:商户名称 extReserved:商户保留信息,回调给商户服务端,sign 签名
// HuaweiPush.prototype.pay = function (payReq, success, error) {
//     if (this.isAndroidDevice()) {
//         exec(success, error, "CordovaHuaweiHMS", "pay", [payReq]);
//     }
// }
// //输出调试日志
// HuaweiPush.prototype.log = function (logStr) {
//     if (this.isAndroidDevice()) {
//         try {
//             cordova.fireDocumentEvent('huaweipush.log', logStr);
//         } catch (exception) {
//             console.log('HuaweiPush:tokenRegistered ' + exception);
//         }
//     }
// }
// //配置 userId  appId rsaKeyPrivate rsaKeyPublic
// HuaweiPush.prototype.config = function (configInfo, success, error) {
//     if (this.isAndroidDevice()) {
//         exec(success, error, "CordovaHuaweiHMS", "config", [configInfo]);
//     }
// }

// HuaweiPush.prototype.getSignData = function (payReq, success, error) {
//     if (this.isAndroidDevice()) {
//         exec(success, error, "CordovaHuaweiHMS", "getSignData", [payReq]);
//     }

// }


// module.exports = new HuaweiPush();




