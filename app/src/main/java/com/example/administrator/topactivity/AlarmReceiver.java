package com.example.administrator.topactivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.administrator.topactivity.log.NgdsLog;

/**
 * Created by wangyt on 2016/1/29.
 * : 各种触发以及闹钟的intent接收器
 */
public class AlarmReceiver extends BroadcastReceiver {
    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        NgdsLog.initFileLoger(context, TAG);
        String action = intent.getAction();
        if (null == action) {
            NgdsLog.e(TAG, "action is null");
            return;
        }
        if (action.equals(CheckService.HEART_BEAT_ACTION)) {
            NgdsLog.e(TAG, "beat alarm");
            Intent startIntent = new Intent(context, CheckService.class);
            context.startService(startIntent);
        } else {
            NgdsLog.e(TAG, "others");
            Intent startIntent = new Intent(context, CheckService.class);
            context.startService(startIntent);
        }
    }
}
