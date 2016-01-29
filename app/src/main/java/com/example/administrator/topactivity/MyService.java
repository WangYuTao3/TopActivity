package com.example.administrator.topactivity;

import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wangyt on 2015/11/30.
 * : description
 */
public class MyService extends Service {

    private Handler mHandler;
    private ActivityManager mActivityManager;
    private static final String LIGHTFLOW_ACCESSIBILITY_SERVICE = "com.example.administrator.topactivity/com.example.administrator.topactivity.HookService";

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
        mActivityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
//        new Thread(new DetectCalendarLaunchRunnable()).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (!Utils.isAccessibilityEnabled(MyService.this, LIGHTFLOW_ACCESSIBILITY_SERVICE)) {
                        Intent intent1 = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        MyService.this.startActivity(intent1);
                        Toast.makeText(MyService.this, "请开启检测组件", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class DetectCalendarLaunchRunnable implements Runnable {

        @Override
        public void run() {
            String[] activePackages;
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
                activePackages = getActivePackages();
            } else {
                activePackages = getActivePackagesCompat();
            }
            for (String activePackage : activePackages) {
                Log.d("wyt", activePackage);
            }
            mHandler.postDelayed(this, 1000);
        }

        String[] getActivePackagesCompat() {
            final List<ActivityManager.RunningTaskInfo> taskInfo = mActivityManager.getRunningTasks(1);
            final ComponentName componentName = taskInfo.get(0).topActivity;
            final String[] activePackages = new String[1];
            activePackages[0] = componentName.getPackageName();
            return activePackages;
        }

        String[] getActivePackages() {
            final Set<String> activePackages = new HashSet<>();
            final List<ActivityManager.RunningAppProcessInfo> processInfos = mActivityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    activePackages.addAll(Arrays.asList(processInfo.pkgList));
                }
            }
            return activePackages.toArray(new String[activePackages.size()]);
        }
    }
}
