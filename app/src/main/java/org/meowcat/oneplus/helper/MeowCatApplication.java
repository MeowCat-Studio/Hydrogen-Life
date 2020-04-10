/*
 * Copyright (c) 2013-2019 MeowCat Studio Powered by MlgmXyysd All Rights Reserved.
 */

package org.meowcat.oneplus.helper;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class MeowCatApplication extends Application {

    public static final String TAG = "OnePlusHelper";

    @SuppressLint("StaticFieldLeak")
    public static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        CrashHandler.getInstance().init(getApplicationContext());
    }
}