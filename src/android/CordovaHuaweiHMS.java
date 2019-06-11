package com.waitaction.huaweipush;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.huawei.android.hms.agent.HMSAgent;
import com.huawei.hms.api.HuaweiApiClient;
import com.huawei.hms.api.ConnectionResult;
import com.huawei.hms.support.api.client.Status;
import com.huawei.hms.support.api.entity.pay.HwPayConstant;
import com.huawei.hms.support.api.entity.pay.PayReq;
import com.huawei.hms.support.api.entity.pay.PayStatusCodes;
import com.huawei.hms.support.api.pay.HuaweiPay;
import com.huawei.hms.support.api.pay.PayResult;
import com.huawei.hms.support.api.push.HuaweiPush;
import com.huawei.hms.support.api.push.TokenResult;
import com.huawei.hms.support.api.client.PendingResult;
import com.huawei.hms.support.api.client.ResultCallback;
import android.content.IntentSender;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.lang.Thread;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;


public class CordovaHuaweiHMS extends CordovaPlugin implements HuaweiApiClient.ConnectionCallbacks,
        HuaweiApiClient.OnConnectionFailedListener {
    public static HuaweiApiClient huaweiApiClient;
    // 接收Push消息
    public static final int RECEIVE_PUSH_MSG = 0x100;
    // 接收Push Token消息
    public static final int RECEIVE_TOKEN_MSG = 0x101;
    // 接收Push 自定义通知消息内容
    public static final int RECEIVE_NOTIFY_CLICK_MSG = 0x102;
    public static final int RECEIVE_TAG_MSG = 0x103;
    public static final int RECEIVE_STATUS_MSG = 0x104;
    public static final int OTHER_MSG = 0x105;
    public static final String NORMAL_MSG_ENABLE = "normal_msg_enable";
    public static final String NOTIFY_MSG_ENABLE = "notify_msg_enable";
    public static String TAG = "HuaweiPushPlugin";
    public static String token = "";
    public static int openNotificationId = 0;
    public static String openNotificationExtras;
    private static CordovaHuaweiHMS instance;
    private static Activity activity;
    private CallbackContext initCallback;
    private static String userId;
    private static String appId;
    private static boolean isDebug;
    private final int REQ_CODE_PAY = 4001;

    public CordovaHuaweiHMS() {
        instance = this;


    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        activity = cordova.getActivity();
        //如果是首次启动，并且点击的通知消息，则处理消息
        if (openNotificationExtras != null || openNotificationExtras != "") {
            notificationOpened(openNotificationId, openNotificationExtras);
        }
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("init")) {
            this.init(callbackContext);
            return true;
        }
        //连接到hms
        if (action.equals("connect")) {
            this.init(callbackContext);
            return true;
        }

        //调用支付
        if (action.equals("pay")) {
            JSONObject jsPayReq = args.getJSONObject(0);
            PayReq aPayReq = new PayReq();
            aPayReq.amount = jsPayReq.getString("amount");
            aPayReq.productDesc = jsPayReq.getString("productDesc");
            aPayReq.requestId = jsPayReq.getString("requestId");
            aPayReq.merchantName = jsPayReq.getString("merchantName");
            aPayReq.extReserved = jsPayReq.getString("extReserved");
            aPayReq.productName = jsPayReq.getString("productName");
            aPayReq.sign = jsPayReq.getString("sign");
            this.log("付款金额:" + aPayReq.amount);
            this.log("商品名称:" + aPayReq.productName);
            this.log("商品描述:" + aPayReq.productDesc);
            this.log("商户名:" + aPayReq.merchantName);
            this.log("订单号:" + aPayReq.requestId);
            this.log("商户备注:" + aPayReq.extReserved);
            this.log("开始付款操作");
            this.pay(callbackContext, aPayReq);
            return true;
        }

        //是否已连接到hms服务
        if (action.equals("isConnected")) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("result", huaweiApiClient.isConnected());
            callbackContext.success(jsonObject);
            return true;
        }
        //配置hms
        if (action.equals("config")) {
            try {
                JSONObject configInfo = args.getJSONObject(0);
                userId = configInfo.getString("userId");
                appId = configInfo.getString("appId");
                isDebug = configInfo.getBoolean("isDebug");
                this.log("基础配置hms");
                callbackContext.success();
            } catch (Exception ex) {
                callbackContext.error(ex.getMessage());
            }

            return true;
        }

        //获取支付签名前的连接字符串
        if (action.equals("getSignData")) {
            JSONObject jsPayReq = args.getJSONObject(0);
            PayReq aPayReq = new PayReq();
            aPayReq.amount = jsPayReq.getString("amount");
            aPayReq.productDesc = jsPayReq.getString("productDesc");
            aPayReq.requestId = jsPayReq.getString("requestId");
            aPayReq.merchantName = jsPayReq.getString("merchantName");
            aPayReq.extReserved = jsPayReq.getString("extReserved");
            aPayReq.productName = jsPayReq.getString("productName");
            this.log("获取签名前的连接字符串");
            this.getSignData(callbackContext, aPayReq);
            return true;
        }

        if (action.equals("stop")) {
            this.delToken(callbackContext);
        }
        return false;
    }


    private void getSignData(CallbackContext callbackContext, PayReq payReq) throws JSONException {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(HwPayConstant.KEY_MERCHANTID, userId);
        params.put(HwPayConstant.KEY_APPLICATIONID, appId);
        params.put(HwPayConstant.KEY_AMOUNT, payReq.amount);
        params.put(HwPayConstant.KEY_PRODUCTNAME, payReq.productName);
        params.put(HwPayConstant.KEY_PRODUCTDESC, payReq.productDesc);
        params.put(HwPayConstant.KEY_REQUESTID, payReq.requestId);
        params.put(HwPayConstant.KEY_SDKCHANNEL, 1);
        params.put(HwPayConstant.KEY_URLVER, "2");
        String noSign = CipherUtil.getSignData(params);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", noSign);
        callbackContext.success(jsonObject);
    }

    private void log(String msg) {
        if (instance == null) {
            return;
        }
        if (isDebug == false) {
            return;
        }
        try {
            JSONObject object = new JSONObject();
            object.put("log", msg);
            String format = "window.cordova.plugins.huaweipush.log(%s);";
            final String js = String.format(format, object.toString());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    instance.webView.loadUrl("javascript:" + js);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {

        }
    }


    private void init(CallbackContext callbackContext) {
        this.log("初始化hms连接");
        HMSAgent.checkUpdate(this.cordova.getActivity(), new UpdateApp()  );
        if (huaweiApiClient != null && huaweiApiClient.isConnected()) {

        } else {
            this.log("正在连接hms");
            huaweiApiClient = new HuaweiApiClient.Builder(this.cordova.getActivity())
                    .addApi(HuaweiPush.PUSH_API)
                    .addApi(HuaweiPay.PAY_API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            //huaweiApiClient.connect();
            huaweiApiClient.connect(this.cordova.getActivity());
        }
        this.initCallback = callbackContext;
        //callbackContext.success();
    }

    public void pay(final CallbackContext callbackContext, PayReq aPayReq) {

        if (!huaweiApiClient.isConnected()) {
            this.log("未连接到hms服务");
            return;
        }
        this.log("已连接到hms服务");
        String productName = aPayReq.productName;
        String productDesc = aPayReq.productDesc;
        String requestId = aPayReq.requestId;
        final PayReq payReq = new PayReq();
        payReq.productName = productName;
        payReq.productDesc = productDesc;
        payReq.merchantId = userId;
        payReq.applicationID = appId;
        payReq.amount = aPayReq.amount;
        payReq.requestId = requestId;
        payReq.sdkChannel = 1;
        payReq.urlVer = "2";
        payReq.sign = aPayReq.sign;
        payReq.merchantName = aPayReq.merchantName;
        payReq.serviceCatalog = "1";
        payReq.extReserved = aPayReq.extReserved;
        this.log("开始支付");
        PendingResult<PayResult> payResultPending = HuaweiPay.HuaweiPayApi.pay(huaweiApiClient, payReq);
        this.log("设置调用支付回调");
        payResultPending.setResultCallback(new ResultCallback<PayResult>() {
            @Override
            public void onResult(PayResult payResult) {
                instance.log("已运行调用支付回调方法");
                Status status = payResult.getStatus();
                if (PayStatusCodes.PAY_STATE_SUCCESS == status.getStatusCode()) {
                    instance.log("调用支付界面成功(此时还未支付)");
                    try {
                        status.startResolutionForResult(activity, REQ_CODE_PAY);
                    } catch (IntentSender.SendIntentException e) {
                        instance.log("启动华为支付失败:" + e.getMessage());
                    } finally {
                        callbackContext.success(status.getStatusCode());
                    }
                } else {
                    // TODO 根据返回码处理错误信息
                    instance.log("调用支付失败:" + status.getStatusCode() + "，错误描述：" + status.getStatusMessage());
                    callbackContext.error(status.getStatusMessage());
                }
            }
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        huaweiApiClient = null;
    }

    public void onConnectionFailed(ConnectionResult result) {
    }

    public void onConnected() {
        this.getToken();
    }




    private void getToken() {
        if (!huaweiApiClient.isConnected()) {
            initCallback.error("{status:\"failed\"}");
            return;
        }
        // 异步调用方式
        try {
            // 异步调用方式
            PendingResult<TokenResult> tokenResult = HuaweiPush.HuaweiPushApi.getToken(huaweiApiClient);
            tokenResult.setResultCallback(new ResultCallback<TokenResult>() {
                @Override
                public void onResult(TokenResult result) {
                }

            });
            initCallback.success("{status:\"success\"}");
        } catch (Exception e) {
            initCallback.error("{status:\"failed\"}");
            Log.e(TAG, e.toString(), e);
        }
    }

    public void onConnectionSuspended(int cause) {
    }

    public static void onTokenRegistered(String regId) {
        Log.e(TAG, "-------------onTokenRegistered------------------" + regId);
        if (instance == null) {
            return;
        }
        try {
            JSONObject object = new JSONObject();
            object.put("token", regId);
            String format = "window.cordova.plugins.huaweipush.tokenRegistered(%s);";
            final String js = String.format(format, object.toString());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    instance.webView.loadUrl("javascript:" + js);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void delToken(final CallbackContext callbackContext) {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (token != "" && null != huaweiApiClient) {
                        HuaweiPush.HuaweiPushApi.deleteToken(huaweiApiClient, token);
                        callbackContext.success();
                    } else {
                        Log.w(TAG, "delete token's params is invalid.");
                        callbackContext.error("token not exists");
                    }
                } catch (Exception e) {
                    callbackContext.error("error occered when delete token");
                    Log.e("PushLog", "delete token exception, " + e.toString());
                }
            }
        }.start();
    }

    public static void pushMsgReceived(String msg) {
        Log.e(TAG, "-------------onTokenRegistered------------------" + msg);
        if (instance == null) {
            return;
        }
        try {
            JSONObject object = new JSONObject();
            object.put("extras", msg);
            String format = "window.cordova.plugins.huaweipush.pushMsgReceived(%s);";
            final String js = String.format(format, object.toString());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    instance.webView.loadUrl("javascript:" + js);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void notificationOpened(int notifyId, String msg) {
        CordovaHuaweiHMS.openNotificationId = notifyId;
        CordovaHuaweiHMS.openNotificationExtras = msg;
        Log.e(TAG, "-------------onTokenRegistered------------------" + msg);
        if (instance == null) {
            return;
        }
        try {
            JSONObject object = new JSONObject();
            object.put("extras", msg);
            String format = "window.cordova.plugins.huaweipush.notificationOpened(%s);";
            final String js = String.format(format, object.toString());
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    instance.webView.loadUrl("javascript:" + js);
                    CordovaHuaweiHMS.openNotificationId = 0;
                    CordovaHuaweiHMS.openNotificationExtras = "";
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public static class CipherUtil {
        private static final String SIGN_ALGORITHMS = "SHA256WithRSA";

        public static String rsaSign(String content, String privateKey) {
            if (null == content || null == privateKey) {
                return null;
            }
            String charset = "UTF-8";
            try {
                PKCS8EncodedKeySpec priPKCS8 = new PKCS8EncodedKeySpec(Base64.decode(privateKey, Base64.DEFAULT));
                KeyFactory keyf = KeyFactory.getInstance("RSA");
                PrivateKey priKey = keyf.generatePrivate(priPKCS8);
                java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);
                signature.initSign(priKey);
                signature.update(content.getBytes(charset));
                byte[] signed = signature.sign();
                return Base64.encodeToString(signed, Base64.DEFAULT);
            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, "sign NoSuchAlgorithmException");
            } catch (InvalidKeySpecException e) {
                Log.e(TAG, "sign InvalidKeySpecException");
            } catch (InvalidKeyException e) {
                Log.e(TAG, "sign InvalidKeyException");
            } catch (SignatureException e) {
                Log.e(TAG, "sign SignatureException");
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "sign UnsupportedEncodingException");
            }
            return null;
        }

        public static boolean doCheck(String content, String sign, String publicKey) {
            if (TextUtils.isEmpty(publicKey)) {
                Log.e(TAG, "publicKey is null");
                return false;
            }

            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                byte[] encodedKey = Base64.decode(publicKey, Base64.DEFAULT);
                PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(encodedKey));

                java.security.Signature signature = java.security.Signature.getInstance(SIGN_ALGORITHMS);

                signature.initVerify(pubKey);
                signature.update(content.getBytes("utf-8"));

                boolean bverify = signature.verify(Base64.decode(sign, Base64.DEFAULT));
                return bverify;

            } catch (NoSuchAlgorithmException e) {
                Log.e(TAG, "doCheck NoSuchAlgorithmException" + e);
            } catch (InvalidKeySpecException e) {
                Log.e(TAG, "doCheck InvalidKeySpecException" + e);
            } catch (InvalidKeyException e) {
                Log.e(TAG, "doCheck InvalidKeyException" + e);
            } catch (SignatureException e) {
                Log.e(TAG, "doCheck SignatureException" + e);
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, "doCheck UnsupportedEncodingException" + e);
            }
            return false;
        }

        /**
         * 对数组里的每一个值从a到z的顺序排序
         *
         * @param Map <String, String>
         * @return String
         */
        public static String getSignData(Map<String, Object> params) {
            StringBuffer content = new StringBuffer();
            // 按照key做排序
            List<String> keys = new ArrayList<String>(params.keySet());
            Collections.sort(keys);
            String value = null;
            Object object = null;
            for (int i = 0; i < keys.size(); i++) {
                String key = (String) keys.get(i);
                object = params.get(key);
                if (object instanceof String) {
                    value = (String) object;
                } else {
                    value = String.valueOf(object);
                }

                if (value != null) {
                    content.append((i == 0 ? "" : "&") + key + "=" + value);
                } else {
                    continue;
                }
            }
            return content.toString();
        }
    }

}
