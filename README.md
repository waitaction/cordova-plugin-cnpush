# 大重构

# 该插件目前完全用不了
# 该插件目前完全用不了
# 该插件目前完全用不了


# 中国大陆地区统一推送cordova插件

## 说明

后端推送因精力问题，暂未开源，可参考华为、极光、苹果开发者官方的相关文档

> 在中国大陆地区使用。
> 如果是华为安卓手机，则插件默认调用华为推送sdk。
> 如果是苹果手机，则插件默认调用苹果推送sdk。
> 其它是其它安卓手机，则插件默认调用极光推送sdk。

## 环境

cordova@9.0.0
cordova-android@8.0.0

## 安装

``` sh
cordova plugin add cordova-plugin-cnpush --variable APPID=HUAWEI_APPID --variable  PACKAGENAME=ANDROID_PACKAGENAME --variable cpid=HUAWEI_CPID --variable JPUSH_APP_KEY=JPUSH_APP_KEY
```

## 调用

``` js
var pushNotification = PushNotification;
window["onNotificationAPN"] = onNotificationAPN;
 pushNotification.register(
    tokenHandler,
    errorHandler,
    {
         "badge": "true",
         "sound": "true",
         "alert": "true",
         "ecb": "onNotificationAPN"
});

/*读取到token*/
function tokenHandler(result) {
    alert('device token = ' + result);
}

function errorHandler(error) {
    alert('error = ' + error);
}

/*接收到通知*/
function onNotificationAPN(event) {
    if (event && event.type == "jpush.openNotification" && event.extras) {
        //极光推送
        //to do
    }
    if (event && event.extras) {
        //华为推送
        //to do
    }
}
```