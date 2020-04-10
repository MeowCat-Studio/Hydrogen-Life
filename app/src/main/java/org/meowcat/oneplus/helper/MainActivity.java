package org.meowcat.oneplus.helper;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.UserHandle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.meowcat.util.PackageUtil;
import org.meowcat.util.PreferencesUtil;

import java.util.List;

import static org.meowcat.util.PreferencesUtil.getFlagState;
import static org.meowcat.util.PreferencesUtil.setFlag;

public class MainActivity extends AppCompatActivity {

    static final String PACKAGE_XPOSED_INSTALLER = "de.robv.android.xposed.installer";
    static final String PACKAGE_EDXPOSED_MANAGER = "org.meowcat.edxposed.manager";
    public static final String mOverSeasAccountFlag = "useOverSeasAccount";
    public static final String mUsePNGScreenshotFormatFlag = "usePNGScreenshotFormat";
//    static final File mDowngradeInstallFlag = new File(BASE_DIR + "downgradeInstall");
//    static final File mAuthCrackFlag = new File(BASE_DIR + "authCrack");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int userId = UserHandle.getUserHandleForUid(MeowCatApplication.context.getApplicationInfo().uid).hashCode();
        final SharedPreferences pref = PreferencesUtil.getPreferences(getApplicationContext());
        final PackageManager packageManager = getPackageManager();
        Toolbar toolbar = findViewById(R.id.toolbar);
        FrameLayout status_container = findViewById(R.id.container_xposed_status);
        ImageView status_icon = findViewById(R.id.icon_xposed_status);
        TextView status_text = findViewById(R.id.text_xposed_status);
        TextView status_info_text = findViewById(R.id.text_xposed_information);
        TextView about_version = findViewById(R.id.text_about_version);
        Switch mAccountSwitch = findViewById(R.id.switch_account_location);
        Switch mHideSwitch = findViewById(R.id.switch_hide_icon);
        Switch mScreenshotFormatSwitch = findViewById(R.id.switch_screenshot_format);
//        Switch mDowngradeSwitch = findViewById(R.id.switch_downgrade_install);
//        Switch mAuthCheckSwitch = findViewById(R.id.switch_auth_crack);

        setSupportActionBar(toolbar);
        about_version.setText(String.format(getString(R.string.about_version), getString(R.string.app_name), BuildConfig.VERSION_NAME, BuildConfig.VERSION_CODE));

        if (getXposedStatus(false)) {
            status_container.setBackgroundColor(getColor(R.color.colorSuccess));
            status_text.setTextColor(getColor(R.color.colorSuccess));
            status_icon.setImageDrawable(getDrawable(R.drawable.ic_success));
            status_text.setText(String.format(getString(R.string.status_xposed_title), getString(R.string.app_name), getString(R.string.status_enabled)));
        } else {
            if (PackageUtil.checkAppInstalled(packageManager, PACKAGE_XPOSED_INSTALLER) || PackageUtil.checkAppInstalled(packageManager, PACKAGE_EDXPOSED_MANAGER)) {
                status_container.setBackgroundColor(getColor(R.color.colorAmber));
                status_text.setTextColor(getColor(R.color.colorAmber));
                status_info_text.setTextColor(getColor(R.color.colorAmber));
                status_icon.setImageDrawable(getDrawable(R.drawable.ic_warning));
                status_text.setText(String.format(getString(R.string.status_xposed_title), getString(R.string.app_name), getString(R.string.status_inactive)));
                status_info_text.setVisibility(View.VISIBLE);
                status_info_text.setText(R.string.status_inactive_desc);
                status_info_text.setOnClickListener(v -> {
                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.toast_module_active), getString(R.string.app_name)), Toast.LENGTH_LONG).show();
                    openXposed(getApplicationContext());
                });
            } else {
                status_container.setBackgroundColor(getColor(R.color.colorError));
                status_text.setTextColor(getColor(R.color.colorError));
                status_info_text.setTextColor(getColor(R.color.colorError));
                status_icon.setImageDrawable(getDrawable(R.drawable.ic_error));
                status_text.setText(String.format(getString(R.string.status_xposed_title), getString(R.string.app_name), getString(R.string.status_disabled)));
                status_info_text.setVisibility(View.GONE);
                status_info_text.setText(R.string.status_inactive_desc);
            }
        }

        mAccountSwitch.setChecked(getFlagState(userId, mOverSeasAccountFlag));
        mAccountSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> setFlag(isChecked, mOverSeasAccountFlag));

        mHideSwitch.setChecked(pref.getBoolean("hide_icon", false));
        mHideSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            pref.edit().putBoolean("hide_icon", isChecked).apply();
            PackageUtil.hideIcon(packageManager, new ComponentName(MainActivity.this, BuildConfig.APPLICATION_ID + ".MainActivity"), isChecked);
        });

//        mDowngradeSwitch.setChecked(mDowngradeInstallFlag.exists());
//        mDowngradeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                try {
//                    if (!Objects.requireNonNull(mDowngradeInstallFlag.getParentFile()).exists()) {
//                        if (!mDowngradeInstallFlag.getParentFile().mkdirs()) {
//                            Log.d(MeowCatApplication.TAG, "Create flag DowngradeInstall parent folder failed");
//                        }
//                    }
//                    if (!mDowngradeInstallFlag.createNewFile()) {
//                        Log.d(MeowCatApplication.TAG, "Create flag DowngradeInstall failed");
//                    }
//                } catch (Exception e) {
//                    Log.d(MeowCatApplication.TAG, "Create flag DowngradeInstall failed: " + e.getLocalizedMessage());
//                }
//            } else {
//                if (!mDowngradeInstallFlag.delete()) {
//                    Log.d(MeowCatApplication.TAG, "Remove flag DowngradeInstall failed");
//                }
//            }
//        });
//
//        mAuthCheckSwitch.setChecked(mAuthCrackFlag.exists());
//        mAuthCheckSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                try {
//                    if (!Objects.requireNonNull(mAuthCrackFlag.getParentFile()).exists()) {
//                        if (!mAuthCrackFlag.getParentFile().mkdirs()) {
//                            Log.d(MeowCatApplication.TAG, "Create flag AuthCrack parent folder failed");
//                        }
//                    }
//                    if (!mAuthCrackFlag.createNewFile()) {
//                        Log.d(MeowCatApplication.TAG, "Create flag AuthCrack failed");
//                    }
//                } catch (Exception e) {
//                    Log.d(MeowCatApplication.TAG, "Create flag AuthCrack failed: " + e.getLocalizedMessage());
//                }
//            } else {
//                if (!mAuthCrackFlag.delete()) {
//                    Log.d(MeowCatApplication.TAG, "Remove flag AuthCrack failed");
//                }
//            }
//        });

        mScreenshotFormatSwitch.setChecked(getFlagState(userId, mUsePNGScreenshotFormatFlag));
        mScreenshotFormatSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> setFlag(isChecked, mUsePNGScreenshotFormatFlag));
    }

    private static void openXposed(Context context) {
        // Check Manager version
        long installedEdXpManagerVer = 1L; // Manager Stub versionCode is always 1
        List<PackageInfo> installedPackages = context.getPackageManager().getInstalledPackages(0); // TODO: may not use flag 0
        for (PackageInfo packageInfo : installedPackages) {
            if (packageInfo.packageName.equals(PACKAGE_EDXPOSED_MANAGER)) {
                installedEdXpManagerVer = packageInfo.getLongVersionCode();
                break;
            }
        }
        if (installedEdXpManagerVer == 1) {
            // EdXposed Manager not found, try to open Xposed Installer
            if (PackageUtil.checkAppInstalled(context.getPackageManager(), PACKAGE_XPOSED_INSTALLER)) {
                // Xposed
                Intent intent = new Intent(PACKAGE_XPOSED_INSTALLER + ".OPEN_SECTION");
                if (context.getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
                    intent = context.getPackageManager().getLaunchIntentForPackage(PACKAGE_XPOSED_INSTALLER);
                }
                if (intent != null) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            .putExtra("section", "modules")
                            .putExtra("fragment", 1)
                            .putExtra("module", BuildConfig.APPLICATION_ID);
                    if (intent.resolveActivity(context.getPackageManager()) != null) {
                        context.startActivity(intent);
                    }
                }
            } else {
                Toast.makeText(context, R.string.edxposed_not_installed, Toast.LENGTH_LONG).show();
            }
        } else {
            // EdXposed
            Intent intent = new Intent(PACKAGE_EDXPOSED_MANAGER + ".OPEN_SECTION");
            if (context.getPackageManager().queryIntentActivities(intent, 0).isEmpty()) {
                intent = context.getPackageManager().getLaunchIntentForPackage(PACKAGE_EDXPOSED_MANAGER);
            }
            if (intent != null) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        .putExtra("section", "modules")
                        .putExtra("fragment", installedEdXpManagerVer > 45500 ? 3 : 1)
                        .putExtra("module", BuildConfig.APPLICATION_ID);
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
            }
        }
    }

    public boolean getXposedStatus(boolean status) {
        return status;
    }
}
