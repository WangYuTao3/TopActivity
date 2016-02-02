package com.example.administrator.topactivity;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.example.administrator.topactivity.log.NgdsLog;

/**
 * Created by wangyt on 2015/11/30.
 * : HookService保活服务
 */
public class CheckService extends Service {
    private static final String TAG = "CheckService";

    public static final String HEART_BEAT_ACTION = "com.wyt.android.intent.alarm";
    private static final String ACCESSIBILITY_SERVICE = "com.example.administrator.topactivity/com.example.administrator.topactivity.HookService";
    private static final int MSG_HANDLE_CHECK = 1;

    private Handler mHandler;
    private Toast mToast;

    @Override
    public void onCreate() {
        super.onCreate();
        NgdsLog.initFileLoger(this, TAG);
        NgdsLog.e(TAG, "onCreate");
        mHandler = new HandleRun();
        mToast = Toast.makeText(this, "请设置为开机启动项", Toast.LENGTH_SHORT);
        Utils.startAlarmAndgetIntent(this, 5000);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NgdsLog.e(TAG, "onStartCommand");
        mHandler.sendEmptyMessageDelayed(MSG_HANDLE_CHECK, 1111);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //fix leak
        mHandler.removeMessages(MSG_HANDLE_CHECK);
        NgdsLog.e(TAG, "onDestroy");
    }

    private void handleCheck() {
        if (!Utils.isAccessibilityEnabled(this, ACCESSIBILITY_SERVICE)) {
            Intent startIntent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startIntent);
        } else if (!Utils.isEnableBootFromPackageName(this, getPackageName())) {
            mToast.show();
        } else {
            mToast.cancel();
        }
        mHandler.sendEmptyMessageDelayed(MSG_HANDLE_CHECK, 1111);
    }


    class HandleRun extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_HANDLE_CHECK:
                    handleCheck();
                    break;
            }
        }
    }
}
