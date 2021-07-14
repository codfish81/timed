package com.example.timed;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.preference.PreferenceManager;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.example.timed.R;
import com.example.timed.ui.DialogActivity;
import com.example.timed.ui.ForegroundServiceActivity;
import com.example.timed.utils.Utils;

import timber.log.Timber;

public class AppTimeLog {

    private static final String TIME_KEY = "time";
    private static final String TARGET_PACKAGE_KEY = "target_package";
    private SharedPreferences preferences;
    private static final String DISPLAY_1_MIN = "display_1_min";

    private int appTimed;
    private boolean hasUsageAccess;
    private String mAppName;
    private Bitmap mAppIcon;

    CountDownTimer cdt = null;
}

    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent == null) {
            stopSelf();
        } else {


            if (intent.getAction() != null && ACTION_STOP_SERVICE.equals(intent.getAction())) {

                if (cdt!=null){
                    cdt.cancel();
                }
                stopForeground(true);
                stopSelf();
            } else {

                initialiseVariables(intent);

                checkIfPermissionGrantedManually();

                fetchAppData();

                runForegroundService();

                setupAndStartCDT();
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void checkIfPermissionGrantedManually() {

        if (!preferences.getBoolean(getString(R.string.usage_permission_pref), false)) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    && hasUsageStatsPermission(this)) {
                preferences.edit().putBoolean(getString(R.string.usage_permission_pref), true).apply();

            }
        }
        hasUsageAccess = preferences.getBoolean(getString(R.string.usage_permission_pref), false);
    }



    private void initialiseVariables(Intent intent) {
        if (cdt != null) {
            cdt.cancel();
        }
        appTimed = intent.getIntExtra(TIME_KEY, 0);
        targetPackage = intent.getStringExtra(TARGET_PACKAGE_KEY);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
    }



    private void setupAndStartCDT() {
        cdt = new CountDownTimer(appTimed, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Timber.i("Countdown seconds remaining in ATDService: %s", millisUntilFinished / 1000);
            }

            @Override
            public void onFinish() {

                Timber.i("Timer finished.Starting activity");

                showStopDialog();

                stopForeground(true);

                stopSelf();
            }
        };
        cdt.start();
    }


    private void showStopDialog() {
        Intent dialogIntent = new Intent(AppTimeDialogService.this, DialogActivity.class);
        dialogIntent.putExtra(TARGET_PACKAGE_KEY, targetPackage);
        dialogIntent.putExtra(APP_COLOR_KEY, mAppColor);
        dialogIntent.putExtra(TEXT_COLOR_KEY, mTextColor);
        dialogIntent.putExtra(CALLING_CLASS_KEY, getClass().getSimpleName());
        dialogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Duration equal to 1 minute
        if (appTime == 60000)
            dialogIntent.putExtra(DISPLAY_1_MIN, false);

        if (hasUsageAccess) {

            // Checks which app is in foreground
            AppChecker appChecker = new AppChecker();
            String packageName = appChecker.getForegroundApp(AppTimeDialogService.this);

            // Creates intent to display
            if (packageName.equals(targetPackage)) {
                Timber.d("App is in use");
                startActivity(dialogIntent);
            } else {
                issueAppStoppedNotification();
            }
        }
        // No usage permission, show dialog without checking foreground app
        else {
            startActivity(dialogIntent);
        }
    }


    @TargetApi(Build.VERSION_CODES.KITKAT)
    boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;
        preferences.edit().putBoolean(getString(R.string.usage_permission_pref), granted).apply();

        return granted;
    }


    private void fetchAppData() {

        ApplicationInfo appInfo;
        PackageManager pm = getPackageManager();

        try {
            Drawable iconDrawable = pm.getApplicationIcon(targetPackage);

            mAppIcon = Utils.getBitmapFromDrawable(iconDrawable);
            appInfo = pm.getApplicationInfo(targetPackage, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            appInfo = null;
            mAppIcon = null;
        }
        mAppName = (String) (appInfo != null ? pm.getApplicationLabel(appInfo) : "(unknown)");

    }

    private void issueAppStoppedNotification() {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);

        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "timesapp_app_stopped";// The id of the channel.
            String channelName = getString(R.string.notif_app_stopped_channel_name);// The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_LOW;
            channelId = createNotificationChannel(CHANNEL_ID, channelName, importance);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(AppTimeDialogService.this, channelId);

        String title = mAppName + " " + getString(R.string.app_closed_notification_title);
        builder.setContentTitle(title)
                .setSmallIcon(R.drawable.app_notification_icon)
                .setContentIntent(PendingIntent.getActivity(AppTimeDialogService.this,
                        0, new Intent(), 0))
                .setLargeIcon(mAppIcon)
                .setColor(getResources().getColor(R.color.colorPrimary))
                .setSubText(getString(R.string.app_closed_notification_subtitle))
                .setAutoCancel(true);
        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        if (notificationManager != null) {
            notificationManager.notify(APP_STOPPED_NOTIF_ID, notification);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;

    }

    private void runForegroundService() {

        String channelId = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = createNotificationChannel("timesapp_fg_service", "Background Service Notification", NotificationManager.IMPORTANCE_LOW);
        }
        Intent notificationIntent = new Intent(this, ForegroundServiceActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId);

        Intent appLaunchIntent = getPackageManager().getLaunchIntentForPackage(targetPackage);

        PendingIntent actionPendingIntent = PendingIntent.getActivity(this, 1, appLaunchIntent, 0);
        NotificationCompat.Action.Builder actionBuilder =
                new NotificationCompat.Action.Builder(R.drawable.ic_exit_to_app_black_24dp,
                        "Return to " + mAppName, actionPendingIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_SERVICE);
        }
        if (preferences.getBoolean(getString(R.string.pref_notification_done_key), true)) {
            Intent stopSelfIntent = new Intent(this, AppTimeDialogService.class);
            stopSelfIntent.setAction(ACTION_STOP_SERVICE);
            PendingIntent stopSelfPIntent = PendingIntent.getService(this, 0, stopSelfIntent, PendingIntent.FLAG_CANCEL_CURRENT);
            NotificationCompat.Action.Builder stopActionBuilder =
                    new NotificationCompat.Action.Builder(R.drawable.ic_clear_black_24dp,
                            "Done", stopSelfPIntent);
            builder.addAction(stopActionBuilder.build());
        }
        Notification notification = builder.setOngoing(true)
                .setContentText(getString(R.string.app_running_service_notif_text))
                .setSubText(getString(R.string.tap_for_more_info_foreground_notif))
                .setColor(getResources().getColor(R.color.colorPrimary))
                .addAction(actionBuilder.build())
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.drawable.app_notification_icon)
                .setContentIntent(pendingIntent).build();

        startForeground(FOREGROUND_NOTIF_ID, notification);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName, int importance) {

        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, importance);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (manager != null) {
            manager.createNotificationChannel(chan);
        }
        return channelId;
    }
}

