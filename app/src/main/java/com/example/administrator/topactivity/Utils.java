package com.example.administrator.topactivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by wangyt on 2016/1/29.
 * : description
 */
public class Utils {

    public static PendingIntent startAlarmAndgetIntent(Context appContext , long repeatPeroid){
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;
        alarmMgr = (AlarmManager) appContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(appContext, AlarmReceiver.class);
        intent.setAction(CheckService.HEART_BEAT_ACTION);
        alarmIntent = PendingIntent.getBroadcast(appContext, 0, intent, 0);
        try {
            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, SystemClock.currentThreadTimeMillis(), repeatPeroid, alarmIntent);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        return alarmIntent;
    }

    public static boolean isAccessibilityEnabled(Context context, String name) {
        int accessibilityEnabled = 0;
        boolean accessibilityFound = false;
        try {
            accessibilityEnabled = Settings.Secure.getInt(context.getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    if (accessabilityService.equalsIgnoreCase(name)) {
                        accessibilityFound = true;
                    }
                }
            }
        }
        return accessibilityFound;
    }


    public static String getTopActPkgNameFromProcess(Context context) {
        ActivityManager.RunningAppProcessInfo currentInfo = null;
        Field field = null;
        int START_TASK_TO_FRONT = 2;
        String pkgName = null;
        try {
            field = ActivityManager.RunningAppProcessInfo.class.getDeclaredField("processState");
        } catch (Exception e) {
            return null;
        }
        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
        if (am == null) {
            return null;
        }
        List<ActivityManager.RunningAppProcessInfo> appList = am.getRunningAppProcesses();
        if (appList == null || appList.isEmpty()) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo app : appList) {
            if (app != null && app.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                Integer state = null;
                try {
                    state = field.getInt(app);
                } catch (Exception e) {
                    return null;
                }
                if (state != null && state == START_TASK_TO_FRONT) {
                    currentInfo = app;
                    break;
                }
            }
        }
        if (currentInfo != null) {
            pkgName = currentInfo.processName;
        }
        return pkgName;
    }

    //useless
    public static String getTopActClassNameFromADB(Context context) {
        String result = "";
        try {
            result = run(new String[]{"dumpsys activity a|grep mFocusedActivity"}, Environment.getExternalStorageDirectory().getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] shorts = result.split(" ");
        if (shorts.length >= 4) {
            result = shorts[3];
        }
        return result;
    }

    private static synchronized String run(String[] cmd, String workdirectory)
            throws IOException {
        StringBuffer result = new StringBuffer();
        try {
            ProcessBuilder builder = new ProcessBuilder(cmd);
            InputStream in = null;
            if (workdirectory != null) {
                builder.directory(new File(workdirectory));
                builder.redirectErrorStream(true);
                Process process = builder.start();
                in = process.getInputStream();
                byte[] re = new byte[1024];
                while (in.read(re) != -1) {
                    result = result.append(new String(re));
                }
            }
            if (in != null) {
                in.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return result.toString();
    }

    //useless
    public static String getTopActClassNameFromPkgName(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        Log.e("wyt", "packageName:" + packageName);
        String result = "empty";
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setPackage(packageName);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            Class<?>[] cc = new Class[2];
            cc[0] = Intent.class;
            cc[1] = int.class;
            Method method = PackageManager.class.getDeclaredMethod("resolveActivity", cc);
            ResolveInfo resolveInfo = (ResolveInfo) method.invoke(packageManager, intent, PackageManager.GET_INTENT_FILTERS);
            if (resolveInfo != null) {
                result = resolveInfo.activityInfo.name;
                Log.e("wyt", "className:" + result);
            } else {
                Log.e("wyt", "componentName null");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("wyt", e.getMessage());
        }
        return result;
    }
}
