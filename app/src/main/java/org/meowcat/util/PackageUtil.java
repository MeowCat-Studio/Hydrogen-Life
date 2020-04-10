package org.meowcat.util;

import android.content.ComponentName;
import android.content.pm.PackageManager;

public class PackageUtil {

    public static boolean checkAppInstalled(PackageManager packageManager, String pkgName) {
        if (pkgName == null || pkgName.isEmpty()) {
            return false;
        }
        try {
            packageManager.getPackageInfo(pkgName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void hideIcon(PackageManager packageManager, ComponentName componentName, boolean hide) {
        packageManager.getComponentEnabledSetting(componentName);
        if (hide) {
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        } else {
            packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT, PackageManager.DONT_KILL_APP);
        }
    }
}
