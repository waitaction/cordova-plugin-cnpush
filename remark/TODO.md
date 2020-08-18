1.每台设备会拥用一个推送的token做为唯一标识
2.服务端会根据这个token推送消息到设备

keytool -list -v -keystore /Users/chenqj/Desktop/demo/testhms/listing.keystore

30:17:B2:E5:F3:04:3B:BB:11:B9:65:5D:A9:52:5D:23:37:B9:3C:4A:A1:D7:00:05:35:C6:9B:7B:59:43:BB:4E


https://developer.huawei.com/consumer/cn/codelab/HMSPreparation/index.html#8



https://developer.huawei.com/consumer/cn/codelab/HMSPreparation/index.html

0866954030682786300007840200CN01

步骤

1.在应用级别的build.gradle 添加

 apply plugin: 'com.huawei.agconnect'

2.在项目级别的build.gradle 添加

依赖项：classpath 'com.huawei.agconnect:agcp:1.4.0.300'
maven仓库：maven { url 'https://developer.huawei.com/repo/' }

3.在应用级别依赖项节点 dependencies 下添加
    
依赖项： implementation 'com.huawei.hms:push:4.0.3.301'

4.在应用级别的根目录下添加 agconnect-services.json

5.在 AndroidManifest.xml 添加服务

<service
    android:name="com.lifang123.huaweipush.HuaHuiPushService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.huawei.push.action.MESSAGING_EVENT" />
    </intent-filter>
</service>

5.添加HuaweiPushService.java ，代码如下

``` java
package com.lifang123.push;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;
import com.huawei.hms.push.SendException;
import java.util.Arrays;

public class HuaweiPushService extends HmsMessageService {
    private static final String TAG = "PushDemoLog";
    private final static String CODELABS_ACTION = "com.huawei.codelabpush.action";

    @Override
    public void onNewToken(String token) {
        Log.i(TAG, "received refresh token:" + token);
        if (!TextUtils.isEmpty(token)) {
            refreshedTokenToServer(token);
        }
        Intent intent = new Intent();
        intent.setAction(CODELABS_ACTION);
        intent.putExtra("method", "onNewToken");
        intent.putExtra("msg", "onNewToken called, token: " + token);
        sendBroadcast(intent);
    }
    private void refreshedTokenToServer(String token) {
        Log.i(TAG, "sending token to server. token:" + token);
    }

    @Override
    public void onMessageReceived(RemoteMessage message) {
        Log.i(TAG, "onMessageReceived is called");
        if (message == null) {
            Log.e(TAG, "Received message entity is null!");
            return;
        }

        Log.i(TAG, "getCollapseKey: " + message.getCollapseKey()
                + "\n getData: " + message.getData()
                + "\n getFrom: " + message.getFrom()
                + "\n getTo: " + message.getTo()
                + "\n getMessageId: " + message.getMessageId()
                + "\n getOriginalUrgency: " + message.getOriginalUrgency()
                + "\n getUrgency: " + message.getUrgency()
                + "\n getSendTime: " + message.getSentTime()
                + "\n getMessageType: " + message.getMessageType()
                + "\n getTtl: " + message.getTtl());

        RemoteMessage.Notification notification = message.getNotification();
        if (notification != null) {
            Log.i(TAG, "\n getImageUrl: " + notification.getImageUrl()
                    + "\n getTitle: " + notification.getTitle()
                    + "\n getTitleLocalizationKey: " + notification.getTitleLocalizationKey()
                    + "\n getTitleLocalizationArgs: " + Arrays.toString(notification.getTitleLocalizationArgs())
                    + "\n getBody: " + notification.getBody()
                    + "\n getBodyLocalizationKey: " + notification.getBodyLocalizationKey()
                    + "\n getBodyLocalizationArgs: " + Arrays.toString(notification.getBodyLocalizationArgs())
                    + "\n getIcon: " + notification.getIcon()
                    + "\n getSound: " + notification.getSound()
                    + "\n getTag: " + notification.getTag()
                    + "\n getColor: " + notification.getColor()
                    + "\n getClickAction: " + notification.getClickAction()
                    + "\n getChannelId: " + notification.getChannelId()
                    + "\n getLink: " + notification.getLink()
                    + "\n getNotifyId: " + notification.getNotifyId());
        }

        Intent intent = new Intent();
        intent.setAction(CODELABS_ACTION);
        intent.putExtra("method", "onMessageReceived");
        intent.putExtra("msg", "onMessageReceived called, message id:" + message.getMessageId() + ", payload data:"
                + message.getData());

        sendBroadcast(intent);

        Boolean judgeWhetherIn10s = false;

        if (judgeWhetherIn10s) {
            startWorkManagerJob(message);
        } else {
            processWithin10s(message);
        }
    }

    private void startWorkManagerJob(RemoteMessage message) {
        Log.d(TAG, "Start new Job processing.");
    }
    private void processWithin10s(RemoteMessage message) {
        Log.d(TAG, "Processing now.");
    }

    @Override
    public void onMessageSent(String msgId) {
        Log.i(TAG, "onMessageSent called, Message id:" + msgId);
        Intent intent = new Intent();
        intent.setAction(CODELABS_ACTION);
        intent.putExtra("method", "onMessageSent");
        intent.putExtra("msg", "onMessageSent called, Message id:" + msgId);
        sendBroadcast(intent);
    }

    @Override
    public void onSendError(String msgId, Exception exception) {
        Log.i(TAG, "onSendError called, message id:" + msgId + ", ErrCode:"
                + ((SendException) exception).getErrorCode() + ", description:" + exception.getMessage());

        Intent intent = new Intent();
        intent.setAction(CODELABS_ACTION);
        intent.putExtra("method", "onSendError");
        intent.putExtra("msg", "onSendError called, message id:" + msgId + ", ErrCode:"
                + ((SendException) exception).getErrorCode() + ", description:" + exception.getMessage());
        sendBroadcast(intent);
    }

    @Override
    public void onTokenError(Exception e) {
        super.onTokenError(e);
    }
}

```

6.添加推送接收器 HuaweiReceiver


``` java
package com.lifang123.huaweipush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class HuaweiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle != null && bundle.getString("msg") != null) {
            String content = bundle.getString("msg");
            //可以输出日志供调试
        }
    }
}
```

7.注册推送服务

``` java
private HuaweiReceiver receiver;
private final static String CODELABS_ACTION = "com.huawei.codelabpush.action";

// 指定的地方调
receiver = new HuaweiReceiver();
IntentFilter filter = new IntentFilter();
filter.addAction(CODELABS_ACTION);
registerReceiver(receiver, filter);
```

8.获取推送唯一的token

``` java
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
    
    public void getToken() {
        new Thread() {
            @Override
            public void run() {
                try {
                    String appId = AGConnectServicesConfig.fromContext(MainActivity.this).getString("client/app_id");
                    String token = HmsInstanceId.getInstance(MainActivity.this).getToken(appId, "HCM");
                } catch (ApiException e) {
                }
            }
        }.start();
    }

```
