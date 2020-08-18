package com.lifang123.push;

import android.content.IntentFilter;
import org.json.JSONArray;
import org.json.JSONException;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;

public class CordovaPush extends CordovaPlugin  {
    private HuaweiReceiver receiver;
    private final static String CODELABS_ACTION = "com.huawei.codelabpush.action";
    private static CordovaPush instance;
    public CordovaPush() {
        instance = this;
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
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

}
