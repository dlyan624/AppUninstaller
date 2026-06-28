package com.cleaner.appuninstaller;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class AppInfo implements Parcelable {
    private String appName;
    private String packageName;
    private Drawable icon;
    private boolean isSelected;
    private boolean isInWhitelist;

    public AppInfo(String appName, String packageName, Drawable icon) {
        this.appName = appName;
        this.packageName = packageName;
        this.icon = icon;
        this.isSelected = false;
        this.isInWhitelist = false;
    }

    protected AppInfo(Parcel in) {
        appName = in.readString();
        packageName = in.readString();
        isSelected = in.readByte() != 0;
        isInWhitelist = in.readByte() != 0;
    }

    public static final Creator<AppInfo> CREATOR = new Creator<AppInfo>() {
        @Override
        public AppInfo createFromParcel(Parcel in) {
            return new AppInfo(in);
        }

        @Override
        public AppInfo[] newArray(int size) {
            return new AppInfo[size];
        }
    };

    public String getAppName() { return appName; }
    public void setAppName(String appName) { this.appName = appName; }

    public String getPackageName() { return packageName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }

    public Drawable getIcon() { return icon; }
    public void setIcon(Drawable icon) { this.icon = icon; }

    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }

    public boolean isInWhitelist() { return isInWhitelist; }
    public void setInWhitelist(boolean inWhitelist) { isInWhitelist = inWhitelist; }

    @Override
    public int describeContents() { return 0; }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appName);
        dest.writeString(packageName);
        dest.writeByte((byte) (isSelected ? 1 : 0));
        dest.writeByte((byte) (isInWhitelist ? 1 : 0));
    }
}
