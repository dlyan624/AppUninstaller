package com.cleaner.appuninstaller;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WhitelistManager {
    private static final String PREFS_NAME = "whitelist_prefs";
    private static final String KEY_WHITELIST = "whitelist_packages";
    
    private Context context;
    private Set<String> whitelistSet;

    public WhitelistManager(Context context) {
        this.context = context.getApplicationContext();
        loadWhitelist();
    }

    public void loadWhitelist() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        whitelistSet = new HashSet<>(prefs.getStringSet(KEY_WHITELIST, new HashSet<>()));
    }

    public void saveWhitelist() {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putStringSet(KEY_WHITELIST, new HashSet<>(whitelistSet));
        editor.commit();
    }

    public boolean addToWhitelist(String packageName) {
        return whitelistSet.add(packageName);
    }

    public boolean removeFromWhitelist(String packageName) {
        return whitelistSet.remove(packageName);
    }

    public boolean isInWhitelist(String packageName) {
        return whitelistSet.contains(packageName);
    }

    public int getWhitelistCount() {
        return whitelistSet.size();
    }

    public List<String> getWhitelistPackages() {
        return new ArrayList<>(whitelistSet);
    }

    /**
     * 获取不在白名单中的应用（即待卸载的应用）
     */
    public List<AppInfo> getAppsNotInWhitelist(List<AppInfo> allApps) {
        List<AppInfo> appsToRemove = new ArrayList<>();
        for (AppInfo app : allApps) {
            if (!isInWhitelist(app.getPackageName())) {
                appsToRemove.add(app);
            }
        }
        return appsToRemove;
    }
}
