/*
 * Copyright (c) 2013-2019 MeowCat Studio Powered by MlgmXyysd All Rights Reserved.
 */

package org.meowcat.bugcatcher;

import android.app.Application;

public class MeowCatApplication extends Application {

    public static final String TAG = "OnePlusHelper";

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.getInstance().init(getApplicationContext());
    }
}