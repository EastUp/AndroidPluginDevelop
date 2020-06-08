package com.east.androidplugindevelop.activity.hook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 * @description: Instrumentation 相当于 Activity 的管理者，Activity 的创建，以及生命周期的调用都是 AMS
 * 通知以后通过 Instrumentation 来调用的。所以需要Hook Instrumentation并替换
 * @author: jamin
 * @date: 2020/6/3
 * |---------------------------------------------------------------------------------------------------------------|
 */
public class HookInstrumentation extends Instrumentation {
    private final String KEY_ORIGINAL_Component = "originalComponent";

    private Context realContext;
    private PluginContext pluginContext;
    private Instrumentation mOldInstrumentation;

    public HookInstrumentation( Context realContext,Instrumentation old,PluginContext pluginContext) {
        this.realContext =realContext;
        this.mOldInstrumentation = old;
        this.pluginContext = pluginContext;
    }

    /**
     * @param realContext 本Apk真正的Context
     */
    // 1.替换所有ActivityThread的mInstrumentation
    public static void hook(Context realContext,PluginContext pluginContext) throws Exception {
        // 1.1获取当前的ActivityThread
        Class<?> clazz = Class.forName("android.app.ActivityThread");
        Field currentActivityThreadField = clazz.getDeclaredField("sCurrentActivityThread");
        currentActivityThreadField.setAccessible(true);
        Object currentActivityThread = currentActivityThreadField.get(null);
        // 1.2获取Activity的mInstrumentation并替换成我们自己的
        Field instrumentationField = clazz.getDeclaredField("mInstrumentation");
        instrumentationField.setAccessible(true);
        Instrumentation old = (Instrumentation) instrumentationField.get(currentActivityThread);
        instrumentationField.set(currentActivityThread, new HookInstrumentation(realContext,old, pluginContext));
    }

    // 2.Hook Instrumentation.execStartActivity()方法改变其Intent,让其骗过AndroidManifest的检测
    //因为这个方法是被隐藏的所以没办法重写，只能自己动手写。
    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        //替换Intent的ComponentName
        hookIntent(intent);
        try {
            Method method = Instrumentation.class.getDeclaredMethod("execStartActivity", Context.class, IBinder.class, IBinder.class,
                    Activity.class, Intent.class, int.class, Bundle.class);
            return (ActivityResult) method.invoke(mOldInstrumentation, who, contextThread, token, target, intent, requestCode, options);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", e.toString());
        }
        return null;
    }

    /**
     * 替换Intent的Component
     */
    private void hookIntent(Intent intent) {
        ComponentName oldComponent = intent.getComponent();
        ComponentName proxyComponent = new ComponentName("com.east.androidplugindevelop",
                "com.east.androidplugindevelop.ProxyActivity"); //坑位
        if (oldComponent != null && (oldComponent.getPackageName() != realContext.getPackageName()
                || oldComponent.getClassName().equals("com.east.androidplugindevelop.activity.plugin.NotRegisterActivity"))) {
            intent.setComponent(proxyComponent);
            intent.putExtra(KEY_ORIGINAL_Component, oldComponent);
        }
    }


    // 3.Hook Instrumentation.newActivity(ClassLoader cl, String className,Intent intent) 还原原来Intent
    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        Activity newActivity;
        ComponentName oldComponent = intent.getParcelableExtra(KEY_ORIGINAL_Component);
        if (oldComponent != null) {
            intent.setComponent(oldComponent);
            className = oldComponent.getClassName();
            newActivity = (Activity) pluginContext.getClassLoader().loadClass(className).newInstance();
        }else{
            newActivity = super.newActivity(cl, className, intent);
        }

        return newActivity;
    }


    // 4.替换资源
    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        Intent intent = activity.getIntent();
        ComponentName oldComponent = intent.getParcelableExtra(KEY_ORIGINAL_Component);
        if (oldComponent != null && !oldComponent.getClassName().equals("com.east.androidplugindevelop.activity.plugin.NotRegisterActivity")) {
            // 在这里进行资源替换
            injectActivityReources(activity);
        }
        super.callActivityOnCreate(activity, icicle);
    }

    //替换资源
    private void injectActivityReources(Activity activity) {
        Context base = activity.getBaseContext();
        try {
            Reflect.on(base).set("mResources", pluginContext.getResources());
            Reflect reflect = Reflect.on(activity);

            // copy theme
            //注意：占坑的Activity的theme和插件的Activity的theme必须一致,
            // 如果没设置请确保宿主和插件application的theme一致
            Resources.Theme theme = pluginContext.getResources().newTheme();
            theme.setTo(activity.getTheme());
            int themeResource = reflect.get("mThemeResource");
            theme.applyStyle(themeResource, true);
            reflect.set("mTheme",theme);

            reflect.set("mResources", pluginContext.getResources());
            reflect.set("mBase", pluginContext);
            reflect.set("mApplication", pluginContext.getApplicationContext());


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
