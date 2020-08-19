package com.lifang123.push;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONObject;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessaging;

public class CordovaPush extends CordovaPlugin {
    private static final String TAG = "HuaweiPushTag";
    private HuaweiReceiver receiver;
    private final static String CODELABS_ACTION = "com.huawei.codelabpush.action";
    public static CordovaPush instance;

    public CordovaPush() {
        instance = this;
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        getHuaweiIntentData(this.cordova.getActivity().getIntent());
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("register")) {
            receiver = new HuaweiReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(CODELABS_ACTION);
            this.cordova.getActivity().registerReceiver(receiver, filter);
            this.getHuaweiToken(callbackContext);
            return true;
        }
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (receiver != null) {
            this.cordova.getActivity().unregisterReceiver(receiver);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        try {
            Log.i(TAG, "CordovaPush onNewIntent");
            this.cordova.getActivity().setIntent(intent);
            this.getHuaweiIntentData(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void getHuaweiToken(CallbackContext callbackContext) {
        new Thread() {
            @Override
            public void run() {
                try {
                    String appId = AGConnectServicesConfig.fromContext(CordovaPush.this.cordova.getActivity()).getString("client/app_id");
                    String token = HmsInstanceId.getInstance(CordovaPush.this.cordova.getActivity()).getToken(appId, "HCM");
                    callbackContext.success(token);
                } catch (ApiException e) {
                    callbackContext.error("");
                }
            }
        }.start();
    }

    public void bridgeWebView(JSONObject object, String bridgeJs) {
        final String js = String.format(bridgeJs, object.toString());
        this.cordova.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                instance.webView.loadUrl("javascript:" + js);
            }
        });
    }

    private void turnOnHuaweiPush() {
        HmsMessaging.getInstance(this.cordova.getActivity()).turnOnPush();
    }

    private void turnOffHuaweiPush() {
        HmsMessaging.getInstance(this.cordova.getActivity()).turnOffPush();
    }


    private void getHuaweiIntentData(Intent intent) {
        if (null != intent) {
//            开发者可以通过如下三行代码获取的值做打点统计
//            String msgid = intent.getStringExtra("_push_msgid");
//            String cmdType = intent.getStringExtra("_push_cmd_type");
//            int notifyId = intent.getIntExtra("_push_notifyid", -1);


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

        } else {
            Log.i(TAG, "intent is null");
        }
    }

}
