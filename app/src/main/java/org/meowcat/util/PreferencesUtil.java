package org.meowcat.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.FileUtils;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.meowcat.oneplus.helper.MeowCatApplication;

import java.io.File;
import java.util.Objects;

import static org.meowcat.oneplus.helper.BuildConfig.APPLICATION_ID;

public class PreferencesUtil {

    public static SharedPreferences getPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("WorldReadableFiles")
    public static void setFlag(boolean isChecked, String flagName) {
        String BASE_DIR = MeowCatApplication.context.getApplicationInfo().deviceProtectedDataDir + "/";
        FileUtils.setPermissions(BASE_DIR, 511, -1, -1);
        final File flag = new File(BASE_DIR, flagName);
        if (isChecked) {
            try {
                if (!Objects.requireNonNull(flag.getParentFile()).exists()) {
                    if (!flag.getParentFile().mkdirs()) {
                        Log.d(MeowCatApplication.TAG, "Create flag " + flag.getName() + " parent folder " + flag.getParent() + " failed");
                    }
                }
                if (!flag.createNewFile()) {
                    Log.d(MeowCatApplication.TAG, "Create flag " + flag.getName() + " failed");
                } else {
                    setFilePermissionsFromMode(flag.getPath(), Context.MODE_WORLD_READABLE);
                }
            } catch (Exception e) {
                Log.d(MeowCatApplication.TAG, "Create flag " + flag.getName() + " failed: " + e.getLocalizedMessage());
            }
        } else {
            if (!flag.delete()) {
                Log.d(MeowCatApplication.TAG, "Remove flag " + flag.getName() + " failed");
            }
        }
    }

    public static boolean getFlagState(int user, String flag) {
        return new File(String.format("/data/user_de/%s/%s/%s", user, APPLICATION_ID, flag)).exists();
    }

    @SuppressWarnings({"deprecation", "SameParameterValue"})
    @SuppressLint({"WorldReadableFiles", "WorldWriteableFiles"})
    private static void setFilePermissionsFromMode(String name, int mode) {
        int perms = FileUtils.S_IRUSR | FileUtils.S_IWUSR
                | FileUtils.S_IRGRP | FileUtils.S_IWGRP;
        if ((mode & Context.MODE_WORLD_READABLE) != 0) {
            perms |= FileUtils.S_IROTH;
        }
        if ((mode & Context.MODE_WORLD_WRITEABLE) != 0) {
            perms |= FileUtils.S_IWOTH;
        }
        FileUtils.setPermissions(name, perms, -1, -1);
    }
}
