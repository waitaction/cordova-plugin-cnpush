var cnPushTool = require('./CnPushTool');


var CordovaPush = function () {

};

function getCordovaPush() {
    var myPush;
    var isUsePush = false
    //华为手机
    if (cnPushTool.isPlatformHuawei()) {
        if (window.huaweiPush) {
            myPush = window.huaweiPush;
            isUsePush = true;
        }
    }

    //oppo手机
    if (cnPushTool.isPlatformOppo()) {
        if (window.oppoPush) {
            myPush = window.oppoPush;
            isUsePush = true;
        }
    }

    //小米手机
    if (cnPushTool.isPlatformXiaomi()) {
        if (window.xiaomiPush) {
            myPush = window.xiaomiPush;
            isUsePush = true;
        }
    }


    if (!isUsePush && !cnPushTool.isPlatformIOS()) {
        if (window.jPush) {
            myPush = window.jPush;
        }
    }

    return myPush;
}

/**
 * 注册推送服务
 * @param successCallback 成功回调
 * @param errorCallback 失败回调
 * @param options 参数
 */
CordovaPush.prototype.register = function (successCallback, errorCallback, options) {
    document.addEventListener('deviceready', function () {
        getCordovaPush().register(successCallback, errorCallback, options);
    }, false);
};

/**
 * 当token变化后，会触发方法的successCallback回调
 * @param successCallback token被自动变更时通知变更后的token
 * @param errorCallback 通知失败的回调
 */
CordovaPush.prototype.onNewToken = function (successCallback, errorCallback) {
    document.addEventListener('deviceready', function () {
        getCordovaPush().onNewToken(successCallback, errorCallback);
    }, false);
};

module.exports = new CordovaPush();









