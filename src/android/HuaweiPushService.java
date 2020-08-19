package com.lifang123.push;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;
import com.huawei.hms.push.SendException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class HuaweiPushService extends HmsMessageService {
    private static final String TAG = "HuaweiPushTag";
    private final static String CODELABS_ACTION = "com.huawei.codelabpush.action";

    @Override
    public void onNewToken(String token) {
        Log.i(TAG, "received refresh token:" + token);
        if (!TextUtils.isEmpty(token)) {
            refreshedTokenToServer(token);
        }
        Intent intent = new Intent();
        intent.setAction(CODELABS_ACTION);
        intent.putExtra("method", "token被刷新");
        intent.putExtra("token", token);
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
        // RemoteMessage.Notification notification = message.getNotification();
        Intent intent = new Intent();
        intent.setAction(CODELABS_ACTION);
        intent.putExtra("_method", "透传消息");
        intent.putExtra("_push_msgid", message.getMessageId());
        intent.putExtra("sendTime", message.getSentTime());

        try {
            JSONObject result = new JSONObject(message.getData());

            for (Iterator itr = result.keys(); itr.hasNext(); ) {

                try {
                    String key = (String) itr.next();
                    String content = result.getString(key);
                    intent.putExtra(key, content);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

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