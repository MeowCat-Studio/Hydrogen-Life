package org.meowcat.oneplus.helper;

import android.app.Application;
import android.content.Context;
import android.os.UserHandle;
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
import static de.robv.android.xposed.XposedHelpers.findClass;
import static de.robv.android.xposed.XposedHelpers.setStaticBooleanField;
import static org.meowcat.util.PreferencesUtil.getFlagState;

public class HookActivity implements IXposedHookLoadPackage {

//    private final Object[] signingDetails = {new Signature[]{new Signature("308203553082023da0030201020204378edaaa300d06092a864886f70d01010b0500305a310d300b0603550406130466616b65310d300b0603550408130466616b65310d300b0603550407130466616b65310d300b060355040a130466616b65310d300b060355040b130466616b65310d300b0603550403130466616b653020170d3138303533303034343434385a180f32313237313230353034343434385a305a310d300b0603550406130466616b65310d300b0603550408130466616b65310d300b0603550407130466616b65310d300b060355040a130466616b65310d300b060355040b130466616b65310d300b0603550403130466616b6530820122300d06092a864886f70d01010105000382010f003082010a0282010100b766ff6afd8a53edd4cee4985bc90e0c515157b5e9f731818961f7250d0d1ac7c7fb80eb5aeb8c28478732e8ff38cff574bfa0eba8039f73af1532f939c4ef9684719efbaba2dd3c583a20907c1c55248a63098c6da23dcfc877763d5fe6061dddd399cf2f49e3250e23f9e687a4d182bcd0662179ba4c9983448e34b4c83e5abbf4f87e87add9157c75fd40de3416744507a3517915f35b6fcad78766e8e1879df8ab823a6ffa335e4790f6e29c87393732025b63ce3a38e42cb0d48cdceb902f191d7d45823db9a0678895e8bfc59b2af7526ca4c2dc3dbe7e70c7c840e666b9629d36e5ddf1d9a80c37f1ab1bc1fb30432914008fbde95d5d3db7853565510203010001a321301f301d0603551d0e04160414d8513e1ae21c64e9ebeee3507e24ea375eef958e300d06092a864886f70d01010b0500038201010088bf20b36428558359536dddcfff16fe233656a92364cb544d8acc43b0859f880a8da339dd430616085edf035e4e6e6dd2281ceb14adde2f05e9ac58d547a09083eece0c6d405289cb7918f85754ee545eefe35e30c103cad617905e94eb4fb68e6920a60d30577855f9feb6e3a664856f74aa9f824aa7d4a3adf85e162c67b9a4261e3185f038ead96112ae3e574d280425e90567352fb82bc9173302122025eaecfabd94d0f9be69a85c415f7cf7759c9651734300952027b316c37aaa1b2418865a3fc7b6bd1072c92ccaacdaa1cf9586d9b8310ceee066ce68859107dfc45ccce729ad9e75b53b584fa37dcd64da8673b1279c6c5861ed3792deac156c8a")}, 1};

    private final int OP_FEATURE_SKU_CHINA = 0;
    private final int OP_FEATURE_SKU_GLOBAL = 1;

    private static final String mOverSeasAccountFlag = "useOverSeasAccount";
    private static final String mUsePNGScreenshotFormatFlag = "usePNGScreenshotFormat";

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) {

        // Remove security flag in any application so you can shot any screen
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

        switch (loadPackageParam.packageName) {
            case BuildConfig.APPLICATION_ID:
                // Hook myself to change activation status
                findAndHookMethod(MainActivity.class.getName(), loadPackageParam.classLoader,
                        "getXposedStatus", boolean.class, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) {
                                NoTaiChi.checkTC(param);
                                param.args[0] = true;
                            }
                        });
                break;
            case "com.android.settings":
                // Unlimited Parallel Applications
                findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        NoTaiChi.checkTC(param);
                        try {
                            findAndHookMethod(((Application) param.thisObject).getClassLoader().loadClass("com.oneplus.settings.apploader.OPApplicationLoader"), "multiAppPackageExcludeFilter", Context.class, String.class, XC_MethodReplacement.returnConstant(true));
                            findAndHookMethod(((Application) param.thisObject).getClassLoader().loadClass("com.oneplus.settings.apploader.OPApplicationLoader"), "packageExcludeFilter", Context.class, String.class, XC_MethodReplacement.returnConstant(true));
                        } catch (ClassNotFoundException e) {
                            XposedBridge.log(e);
                        }
                    }
                });
                break;
            case "com.oneplus.screenshot":
                // Screenshot png format
                findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        NoTaiChi.checkTC(param);
                        Context context = (Context) param.args[0];
                        int userId = UserHandle.getUserHandleForUid(context.getApplicationInfo().uid).hashCode();
                        try {
                            // Make sure png format not include user IMEI water mark
                            findAndHookMethod(((Application) param.thisObject).getClassLoader().loadClass("com.oneplus.screenshot.util.Utils"), "getIMEI", Context.class, XC_MethodReplacement.returnConstant(""));
                            if (getFlagState(userId, mUsePNGScreenshotFormatFlag)) {
                                findAndHookMethod(((Application) param.thisObject).getClassLoader().loadClass("com.oneplus.screenshot.longshot.util.Configs"), "shouldEncrytImage", XC_MethodReplacement.returnConstant(true));
                            } else {
                                findAndHookMethod(((Application) param.thisObject).getClassLoader().loadClass("com.oneplus.screenshot.longshot.util.Configs"), "shouldEncrytImage", XC_MethodReplacement.returnConstant(false));
                            }
                        } catch (ClassNotFoundException e) {
                            XposedBridge.log(e);
                        }
                    }
                });
                break;
            case "com.oneplus.account":
                // OnePlus Account region switch (China/OverSeas)
                findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        NoTaiChi.checkTC(param);
                        Context context = (Context) param.args[0];
                        int userId = UserHandle.getUserHandleForUid(context.getApplicationInfo().uid).hashCode();
                        try {
                            findAndHookMethod(((Application) param.thisObject).getClassLoader().loadClass("com.oneplus.sdk.utils.OpFeatures"), "isSupport", int[].class,  new XC_MethodHook() {
                                        @Override
                                        protected void beforeHookedMethod(MethodHookParam param) {
                                            int iLength = ((int[]) param.args[0]).length;
                                            if (getFlagState(userId, mOverSeasAccountFlag)) {
                                                for (int i = 0; i < iLength; i++) {
                                                    if (((int[]) param.args[0])[i] == OP_FEATURE_SKU_CHINA) {
                                                        param.setResult(false);
                                                    } else if (((int[]) param.args[0])[i] == OP_FEATURE_SKU_GLOBAL) {
                                                        param.setResult(true);
                                                    }
                                                }
                                            } else {
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
                        } catch (ClassNotFoundException e) {
                            XposedBridge.log(e);
                        }
                    }
                });
                break;
            case "net.oneplus.launcher":
                // Launcher leftmost screen Google app
                findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        NoTaiChi.checkTC(param);
                        setStaticBooleanField(findClass("net.oneplus.launcher.config.FeatureFlags", ((Application) param.thisObject).getClassLoader()), "CUSTOMIZE_LEFT_MOST_SCREEN_ENABLED", true);
                    }
                });
                break;
        }
    }

}
