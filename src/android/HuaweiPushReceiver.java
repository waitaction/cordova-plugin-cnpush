/*
 * Copyright (C) Huawei Technologies Co., Ltd. 2016. All rights reserved.
 * See LICENSE.txt for this sample's licensing information.
 */

package com.waitaction.huaweipush;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.content.Intent;

import com.huawei.hms.support.api.push.PushReceiver;
import android.util.Log;

/*
 * 接收Push所有消息的广播接收器
 */
public class HuaweiPushReceiver extends PushReceiver {

    @Override
    public void onToken(Context context, String token, Bundle extras) {
        String belongId = extras.getString("belongId");
        String content = "get token and belongId successful, token = " + token + ",belongId = " + belongId;
        CordovaHuaweiHMS.token = token;
        CordovaHuaweiHMS.onTokenRegistered(token);
        Log.w(CordovaHuaweiHMS.TAG, content);
    }

    @Override
    public boolean onPushMsg(Context context, byte[] msg, Bundle bundle) {
        try {
            String content = "Receive a Push pass-by message： " + new String(msg, "UTF-8");
            CordovaHuaweiHMS.pushMsgReceived(new String(msg, "UTF-8"));
            Log.w(CordovaHuaweiHMS.TAG, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void onEvent(Context context, Event event, Bundle extras) {
        if (Event.NOTIFICATION_OPENED.equals(event) || Event.NOTIFICATION_CLICK_BTN.equals(event)) {
            int notifyId = extras.getInt(BOUND_KEY.pushNotifyId, 0);
            if (0 != notifyId) {
                NotificationManager manager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                manager.cancel(notifyId);
            }
            String content = "receive extented notification message: " + extras.getString(BOUND_KEY.pushMsgKey);
            CordovaHuaweiHMS.openNotificationId = notifyId;
            CordovaHuaweiHMS.openNotificationExtras = extras.getString(BOUND_KEY.pushMsgKey);
            Log.w(CordovaHuaweiHMS.TAG, content);
            CordovaHuaweiHMS.notificationOpened(CordovaHuaweiHMS.openNotificationId,CordovaHuaweiHMS.openNotificationExtras);
        }
        super.onEvent(context, event, extras);
    }

    @Override
    public void onPushState(Context context, boolean pushState) {
        try {
            String content = "The current push status： " + (pushState ? "Connected" : "Disconnected");
            Log.w(CordovaHuaweiHMS.TAG, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
