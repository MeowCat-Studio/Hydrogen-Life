package org.meowcat.oneplus.helper;

import android.app.Application;
import android.content.Context;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import org.meowcat.notaichi.NoTaiChi;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class HookActivity implements IXposedHookLoadPackage {

    private final int OP_FEATURE_SKU_CHINA = 0;
    private final int OP_FEATURE_SKU_GLOBAL = 1;

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam LoadPackageParam) {

        findAndHookMethod(Window.class, "setFlags", int.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                NoTaiChi.checkTC(param);
                Integer flags = (Integer) param.args[0];
                flags &= ~WindowManager.LayoutParams.FLAG_SECURE;
                param.args[0] = flags;
            }
        });

        findAndHookMethod(SurfaceView.class, "setSecure", boolean.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) {
                NoTaiChi.checkTC(param);
                param.args[0] = false;
            }
        });

        if (LoadPackageParam.appInfo == null) {
            return;
        }

        String packageName = LoadPackageParam.packageName;
        ClassLoader classLoader = LoadPackageParam.classLoader;

        if (packageName.equals(BuildConfig.APPLICATION_ID)) {
            findAndHookMethod(BuildConfig.APPLICATION_ID + ".MainActivity", classLoader,
                    "getXposedStatus", boolean.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            NoTaiChi.checkTC(param);
                            param.args[0] = true;
                        }
                    });
        }

        if (packageName.equals("com.android.settings")) {
            findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    NoTaiChi.checkTC(param);
                    try {
                        findAndHookMethod(((Application) param.thisObject).getClassLoader().loadClass("com.oneplus.settings.apploader.OPApplicationLoader"), "multiAppPackageExcludeFilter", Context.class, String.class, XC_MethodReplacement.returnConstant(true));
                    } catch (ClassNotFoundException e) {
                        XposedBridge.log(e);
                    }
                }
            });
        }

        if (packageName.equals("com.oneplus.account")) {
            findAndHookMethod("com.oneplus.sdk.utils.OpFeatures", classLoader,
                    "isSupport", int[].class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            NoTaiChi.checkTC(param);
                            int iLength = ((int[]) param.args[0]).length;
                            if (MainActivity.mOverSeasAccountFlag.exists()) {
                                XposedBridge.log("OnePlusHelper: useOverSeasAccount");
                                for (int i = 0; i < iLength; i++) {
                                    if (((int[]) param.args[0])[i] == OP_FEATURE_SKU_CHINA) {
                                        param.setResult(false);
                                    } else if (((int[]) param.args[0])[i] == OP_FEATURE_SKU_GLOBAL) {
                                        param.setResult(true);
                                    }
                                }
                            } else {
                                XposedBridge.log("OnePlusHelper: useChinaAccount");
                                for (int i = 0; i < iLength; i++) {
                                    if (((int[]) param.args[0])[i] == OP_FEATURE_SKU_CHINA) {
                                        param.setResult(true);
                                    } else if (((int[]) param.args[0])[i] == OP_FEATURE_SKU_GLOBAL) {
                                        param.setResult(false);
                                    }
                                }
                            }
                        }
                    });
        }
    }
}
