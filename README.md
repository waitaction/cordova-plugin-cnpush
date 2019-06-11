``` sh
cordova plugin add cordova-plugin-cnpush --variable APPID=HUAWEI_APPID --variable  PACKAGENAME=ANDROID_PACKAGENAME --variable cpid=HUAWEI_CPID --variable JPUSH_APP_KEY=JPUSH_APP_KEY
```

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

function tokenHandler(result) {
    // Your iOS push server needs to know the token before it can push to this device
    // here is where you might want to send it the token for later use.
    alert('device token = ' + result);
}
// result contains any error description text returned from the plugin call
function errorHandler(error) {
    alert('error = ' + error);
}
// iOS
function onNotificationAPN(event) {
	/*接收到的数据类型 {"sound":"default","body":"body","title":"title","customkey":"test","foreground":"1"}*/
    alert("onNotificationAPN");
    console.log(event);
    if (event.alert) {
       // navigator.notification.alert(event.alert);
    }

    if (event.sound) {
       // var snd = new Media(event.sound);
        //snd.play();
    }

    if (event.badge) {
        //pushNotification.setApplicationIconBadgeNumber(successHandler, errorHandler, event.badge);
    }
}

```