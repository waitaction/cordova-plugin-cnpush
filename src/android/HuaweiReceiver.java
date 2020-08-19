package com.lifang123.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class HuaweiReceiver extends BroadcastReceiver {
    private static final String TAG = "PushDemoLog";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "HuaweiReceiver");

        JSONObject jsonObject = new JSONObject();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                try {
                    String content = bundle.getString(key);
                    jsonObject.put(key, content);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        CordovaPush.instance.bridgeWebView(jsonObject, String.format("cordova.fireDocumentEvent('messageReceived', %s);", jsonObject.toString()));
    }
}