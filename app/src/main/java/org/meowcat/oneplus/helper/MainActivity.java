package org.meowcat.oneplus.helper;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.meowcat.bugcatcher.MeowCatApplication;

import java.io.File;
import java.io.IOException;

import de.robv.android.xposed.XposedBridge;

@SuppressLint("Registered")
public class MainActivity extends AppCompatActivity {

    public static final String BASE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/data/" + BuildConfig.APPLICATION_ID + "/files/";
    static final File mOverSeasAccountFlag = new File(BASE_DIR + "useOverSeasAccount");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean useOverSeasAccount = mOverSeasAccountFlag.exists();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView mXposedTextView = findViewById(R.id.tv_xposed_status);
        Switch mAccountSwitch = findViewById(R.id.switch_account_location);
        if (getXposedStatus(false)) {
            mXposedTextView.setText(R.string.xposed_enabled);
        } else {
            mXposedTextView.setText(R.string.xposed_disabled);
        }
        mAccountSwitch.setChecked(useOverSeasAccount);
        mAccountSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        if (!mOverSeasAccountFlag.getParentFile().exists()) {
                            if (!mOverSeasAccountFlag.getParentFile().mkdirs()) {
                                Log.d(MeowCatApplication.TAG, "Create flag OverSeasAccount parent folder failed");
                            }
                        }
                        if (!mOverSeasAccountFlag.createNewFile()) {
                            Log.d(MeowCatApplication.TAG, "Create flag OverSeasAccount failed");
                        }
                    } catch (IOException e) {
                        Log.d(MeowCatApplication.TAG, "Create flag OverSeasAccount failed: " + e.getLocalizedMessage());
                    }
                } else {
                    if (!mOverSeasAccountFlag.delete()) {
                        Log.d(MeowCatApplication.TAG, "Remove flag OverSeasAccount failed");
                    }
                }
            }
        });
    }

    public boolean getXposedStatus(boolean status) {
        return status;
    }
}
