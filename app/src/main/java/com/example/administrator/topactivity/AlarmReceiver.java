package com.example.administrator.topactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by wangyt on 2016/1/29.
 * : 服务保活Receiver
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (null == action) {
            Log.e("wyt", "action is null");
            return;
        }
        if (action.equals(CheckService.HEART_BEAT_ACTION)) {
            Log.e("wyt", "beat alarm");
            Intent startIntent = new Intent(context, CheckService.class);
            context.startService(startIntent);
        } else {
            Log.e("wyt", "others");
            Intent startIntent = new Intent(context, CheckService.class);
            context.startService(startIntent);
        }
    }
}
