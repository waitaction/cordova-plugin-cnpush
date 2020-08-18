package com.lifang123.push;

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