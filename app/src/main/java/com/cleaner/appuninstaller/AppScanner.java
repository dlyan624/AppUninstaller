package com.cleaner.appuninstaller;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AppScanner {

    public interface ScanCallback {
        void onScanComplete(List<AppInfo> appList);
        void onScanError(String error);
    }

    /**
     * 扫描手机中所有已安装的第三方应用
     */
    public static void scanInstalledApps(Context context, ScanCallback callback) {
        try {
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> packages = pm.getInstalledPackages(0);
            List<AppInfo> appList = new ArrayList<>();

            for (PackageInfo packageInfo : packages) {
                // 只获取第三方应用（非系统应用）
                if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                    String packageName = packageInfo.packageName;
                    Drawable icon = packageInfo.applicationInfo.loadIcon(pm);

                    AppInfo appInfo = new AppInfo(appName, packageName, icon);
                    appList.add(appInfo);
                }
            }

            // 按应用名称排序
            Collections.sort(appList, Comparator.comparing(AppInfo::getAppName, String.CASE_INSENSITIVE_ORDER));

            if (callback != null) {
                callback.onScanComplete(appList);
            }

        } catch (Exception e) {
            if (callback != null) {
                callback.onScanError("扫描应用失败: " + e.getMessage());
            }
        }
    }

    /**
     * 扫描所有应用（包括系统应用）
     */
    public static void scanAllApps(Context context, ScanCallback callback) {
        try {
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> packages = pm.getInstalledPackages(0);
            List<AppInfo> appList = new ArrayList<>();

            for (PackageInfo packageInfo : packages) {
                // 排除本应用自身
                if (!packageInfo.packageName.equals(context.getPackageName())) {
                    String appName = packageInfo.applicationInfo.loadLabel(pm).toString();
                    String packageName = packageInfo.packageName;
                    Drawable icon = packageInfo.applicationInfo.loadIcon(pm);

                    AppInfo appInfo = new AppInfo(appName, packageName, icon);
                    appList.add(appInfo);
                }
            }

            Collections.sort(appList, Comparator.comparing(AppInfo::getAppName, String.CASE_INSENSITIVE_ORDER));

            if (callback != null) {
                callback.onScanComplete(appList);
            }

        } catch (Exception e) {
            if (callback != null) {
                callback.onScanError("扫描应用失败: " + e.getMessage());
            }
        }
    }
}
