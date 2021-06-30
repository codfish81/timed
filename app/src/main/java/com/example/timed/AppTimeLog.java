package com.example.timed;

import androidx.appcombat.app.AppCompatActivity;
public class AppTimeLog {

    private static final String TIME_KEY = "time";
    private static final String TARGET_PACKAGE_KEY = "target_package";
    private SharedPreferences preferences;
    private static final String DISPLAY_1_MIN = "display_1_min";

    private int appTimed.;
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
        cdt = new CountDownTimer(appTimed, 1000) {}
    }
