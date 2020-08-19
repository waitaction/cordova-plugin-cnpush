 # 统一推送

> 暂时只添加了华为推送HMS SDK

## 华为推送配置

在项目根目录建个文件夹 `config-push` ，将 `agconnect-services.json` 拷贝到 `config-push` 目录。

> `agconnect-services.json` 文件，请从华为开发者官网配置推送后下载到本地。
> [华为开发者官网](https://developer.huawei.com/consumer/cn/)

调试推送需要使用签名后的apk，因此建议在 `cordova` 根目录建 `build.json` 文件，把签名信息配置一下

``` json
{
    "android": {
        "debug": {
            "keystore": "./keys/testpush.keystore",
            "alias": "testpush.keystore",
            "storePassword": "123456",
            "password": "123456"
        },
        "release": {
            "keystore": "./keys/testpush.keystore",
            "alias": "testpush.keystore",
            "storePassword": "123456",
            "password": "123456"
        }
    }
}
```
> `build.json`的上述配置需要改成你自已的配置信息

如需调试，需要在真实的设备，使用以下命令调试

``` shell
cordova run android --device --buildConfig
```

## 使用

使用前需注册，以获取 `token` ，你可以将 `token` 与你的app用户信息关联后上传到服务器

``` js
// 注册推送
cnPush.register(function(token) {
    console.log(token);
}, function(err) {
    console.log(err);
}, []);
```

注册完成后，需要监听 `messageReceived` 事件

``` js
document.addEventListener("messageReceived", function(result) {
    console.log(result);
}, false);
```
