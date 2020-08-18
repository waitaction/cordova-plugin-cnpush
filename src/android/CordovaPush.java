package com.lifang123.push;

import org.json.JSONArray;
import org.json.JSONException;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;

public class CordovaPush extends CordovaPlugin  {

    public CordovaPush() {
   
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        //如果是首次启动，并且点击的通知消息，则处理消息
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("init")) {
            return true;
        }
        return false;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
      
    }


}
