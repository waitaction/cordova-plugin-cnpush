 # 统一推送

cordova应用统一推送，安装前需要先安装依赖插件:

安装 `极光推送安卓版本` 插件

``` shell
cordova plugin add cordova-plugin-android-jpush --variable APP_KEY=your_jpush_appkey
```

安装 `华为推送` 插件

``` shell
cordova plugin add cordova-plugin-huawei-push
```

安装 `cordova-plugin-cnpush`
 

``` shell
 cordova plugin add cordova-plugin-cnpush
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
