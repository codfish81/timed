
package com.example.timed;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AppDataManager extends AppCompatActivity {
    public static long DAY_MS = 1000 * 60 * 60 * 24;
    public static long WEEK_MS = DAY_MS * 7;

    private final UsageStatsManager mUsageStatsManager;
    private final PackageManager mPackageManager;

    /**
     * Create and initialize new AppDataManager.
     * @param activity needed to request data and app permissions.
     */
    public AppDataManager(Activity activity){
        mUsageStatsManager = (UsageStatsManager) activity.getSystemService(Context.USAGE_STATS_SERVICE);
        checkPermissions(activity);

        mPackageManager = activity.getApplicationContext().getPackageManager();
    }


    /**
     * The getUsage function is used to request UsageStats history.
     * @param usageLengthMs the amount of time in miliseconds to get data
     * @return a lisy of AppUsage items
     */
    public List<AppUsage> getUsage(long usageLengthMs){
        long endMs = System.currentTimeMillis();
        long beginMs = endMs - usageLengthMs;

        return getUsage(beginMs, endMs);
    }

    public List<AppUsage> getUsageForToDay()
    {
        return getUsage(UsageStatsManager.INTERVAL_DAILY);
    }
    public List<AppUsage> getUsageForWeek()
    {
        return getUsage(UsageStatsManager.INTERVAL_WEEKLY);
    }

    private List<AppUsage> getUsage(int interval){

        HashMap<String, AppUsage> resultMap = new HashMap<>();

        long now = System.currentTimeMillis();
        // queryUsageStats returns incorrect data if begin and ent time are used,
        // use the interval variable instead
        // its a hack but you hav to set some kind of rand within the interval
        final List<UsageStats> stats = mUsageStatsManager.queryUsageStats(interval, now - 10000, now);

        // get app list to resolve app name
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        @SuppressLint("QueryPermissionsNeeded")
        List<ResolveInfo> pkgAppsList = mPackageManager.queryIntentActivities(mainIntent, 0);

        List<ApplicationInfo> packages = mPackageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for(UsageStats stat : stats){
            AppUsage usage = new AppUsage();
            usage.name = null;
            usage.usageMs = stat.getTotalTimeInForeground();

            // try to get app name
            for (ResolveInfo app : pkgAppsList) {
                if (app.activityInfo.packageName.equals(stat.getPackageName())) {
                    usage.name = app.activityInfo.loadLabel(mPackageManager).toString();
                }
            }

            // get app icon
            for (ApplicationInfo applicationInfo : packages) {
                if ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    //usage.name = applicationInfo.loadLabel(mPackageManager).toString();
                    usage.icon = applicationInfo.loadIcon(mPackageManager);
                }
            }

            // only add stats that have usage times greater then 0
            if(usage.usageMs > 1000 && usage.name != null){
                 AppUsage usageExisting = resultMap.get(usage.name);
                 if (usageExisting != null){
                     usageExisting.usageMs = usageExisting.usageMs + usage.usageMs;
                 } else {
                     resultMap.put(usage.name, usage);
                 }
            }
        }
        List<AppUsage> result = new ArrayList<>(resultMap.values());
        Collections.sort(result);
        Collections.reverse(result);

        return result;
    }


    /**
     * Check if permissions are granted and request it if it is not
     * @param  activity needed to request data and app permissions.
     */
    private void checkPermissions(Activity activity) {
        // get permissions status
        Context context = activity.getApplicationContext();

        AppOpsManager appOps = (AppOpsManager) context
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats",
                android.os.Process.myUid(), context.getPackageName());
        boolean granted = mode == AppOpsManager.MODE_ALLOWED;

        // request user to set permissions if not granted
        if (! granted) {
            activity.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    /**
     * The AppUsage is a simple class needed to sort the results and to pass back to application
     */
    public static class AppUsage implements Comparable<AppUsage> {
        String name;
        long usageMs;
        Drawable icon;

        @Override
        public int compareTo(AppUsage o) {
            return (int) (this.usageMs - o.usageMs);
        }
    }

}