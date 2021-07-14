package com.example.timed;

import android.app.Activity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.provider.Settings;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppDataManager extends AppCompatActivity {
    public static long DAY_MS = 1000 * 60 * 60 * 24;
    public static long WEEK_MS = DAY_MS * 7;

    private final UsageStatsManager mUsageStatsManager;
    private final PackageManager mPackageManager;

    public AppDataManager(Activity activity){
        mUsageStatsManager = (UsageStatsManager) activity.getSystemService(Context.USAGE_STATS_SERVICE);
        requestPermissions(activity);

        mPackageManager = activity.getApplicationContext().getPackageManager();
    }


    public List<AppUsage> getUsage(long usageLengthMs){

        ArrayList<AppUsage> result = new ArrayList<>();

        long endMs = System.currentTimeMillis();
        long beginMs = endMs - usageLengthMs;
        final List<UsageStats> stats =
                mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_BEST, beginMs, endMs);

        for(UsageStats stat : stats){
            AppUsage usage = new AppUsage();
            usage.name = getAppName(stat.getPackageName());
            usage.usageMs = stat.getTotalTimeInForeground();

            if(usage.usageMs > 0){
                result.add(usage);
            }
        }

        Collections.sort(result);
        Collections.reverse(result);

        return result;
    }

    private String getAppName(String packageName) {
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> pkgAppsList = mPackageManager.queryIntentActivities(mainIntent, 0);

        for (ResolveInfo app : pkgAppsList) {
            if (app.activityInfo.packageName.equals(packageName)) {
                return app.activityInfo.loadLabel(mPackageManager).toString();
            }
        }
        return packageName;
    }

    private String getAppNames(String packageName){
        try {
            ApplicationInfo info = mPackageManager.getApplicationInfo(packageName, 0);
            return "*" + info.name;
        } catch (PackageManager.NameNotFoundException e) {
            return packageName;
        }
    }

    private void requestPermissions(Activity activity) {
        List<UsageStats> stats = mUsageStatsManager
                .queryUsageStats(UsageStatsManager.INTERVAL_DAILY, 0, System.currentTimeMillis());
        boolean isEmpty = stats.isEmpty();
        if (isEmpty) {
            activity.startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    public static class AppUsage implements Comparable<AppUsage> {
        String name;
        long usageMs;

        @Override
        public int compareTo(AppUsage o) {
            return (int) (this.usageMs - o.usageMs);
        }
    }

}
