
package com.example.timed;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class AppDataManager {
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
        long endMs = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        long beginMs = calendar.getTimeInMillis();

        return getUsage(beginMs, endMs);
    }
    public List<AppUsage> getUsageForWeek()
    {
        long endMs = System.currentTimeMillis();

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        long beginMs = calendar.getTimeInMillis() - 6 * DAY_MS;
        return getUsage(beginMs, endMs);
    }

    public List<AppUsage> getUsage(long beginMs, long endMs){


        HashMap<String, AppUsage> resultMap = new HashMap<>();

        // get all stats
        final List<UsageStats> stats =
                mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginMs, endMs);

        // get app list to resolve app name
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pkgAppsList = mPackageManager.queryIntentActivities(mainIntent, 0);

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

        @Override
        public int compareTo(AppUsage o) {
            return (int) (this.usageMs - o.usageMs);
        }
    }

}