package com.buckylabs.appbackuppro;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;


public class Apk {

    private String AppName;
    private Drawable AppIcon;
    private ApplicationInfo AppInfo;
    private boolean isChecked;


    public Apk(String appName, Drawable appIcon, ApplicationInfo appInfo, boolean isChecked) {
        AppName = appName;
        AppIcon = appIcon;
        AppInfo = appInfo;
        this.isChecked = isChecked;
    }

    public String getAppName() {
        return AppName;
    }

    public void setAppName(String appName) {
        AppName = appName;
    }

    public Drawable getAppIcon() {
        return AppIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        AppIcon = appIcon;
    }

    public ApplicationInfo getAppInfo() {
        return AppInfo;
    }

    public void setAppInfo(ApplicationInfo appInfo) {
        AppInfo = appInfo;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


}