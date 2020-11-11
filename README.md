# 统一推送

安装 `cordova-plugin-cnpush`
 
``` shell
 cordova plugin add cordova-plugin-cnpush
```

> cordova应用统一推送，可以安装下列插件，`cordova-plugin-cnpush`会根据手机型号优先使用`厂商sdk`的cordova插件

## 安卓手机推送(非厂商sdk)

其它安卓手机使用推送，安装 `极光推送安卓版本(免费版)` 插件

``` shell
cordova plugin add cordova-plugin-android-jpush --variable APP_KEY=your_jpush_appkey
```

## 华为推送(厂商sdk)

如需在`华为手机`使用系统级推送，请安装 `华为推送` 插件

``` shell
cordova plugin add cordova-plugin-huawei-push
```

## OPPO推送(厂商sdk)

如需在`OPPO手机`使用系统级推送，请安装 `OPPO推送` 插件

``` shell
cordova plugin add cordova-plugin-oppo-push --variable  APP_KEY=YOUR_APP_KEY --variable APP_SECRET=YOUR_APP_SECRET
```


> 各依赖插件的配置，请参考插件官方文档

## 使用

使用前需注册，以获取 `token` ，你可以将 `token` 与你的app用户信息关联后上传到服务器

``` js
// 注册推送
cnPush.register(function(token) {
    console.log(token);
}, function(err) {
    console.log(err);
}, []);

// 接收token
cnPush.onNewToken(function(token) {
    console.log(token); // 会多次接收到token
});
```

注册完成后，需要监听 `messageReceived` 事件

``` js
document.addEventListener("messageReceived", function(result) {
    console.log(result);
}, false);
```
