package com.east.androidplugindevelop;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
class HookInstrumentation extends Instrumentation {
    private final String KEY_ORIGINAL_Component = "originalComponent";

    private Instrumentation mOldInstrumentation;
    private String proxyClassName; // 坑位Activity的全路径名

    public HookInstrumentation(Instrumentation old, Class proxyClass) {
        this.mOldInstrumentation = old;
        proxyClassName = proxyClass.getCanonicalName();
        Log.d("TAG",proxyClassName);
    }

    /**
     * @param proxyClass 占坑位的class
     * @throws Exception
     */
    // 1.替换所有ActivityThread的mInstrumentation
    public static void hook(Class proxyClass) throws Exception {
        if (!Activity.class.isAssignableFrom(proxyClass))
            throw new RuntimeException("class必须传占坑位Activity的Class");

        // 1.1获取当前的ActivityThread
        Class<?> clazz = Class.forName("android.app.ActivityThread");
        Field currentActivityThreadField = clazz.getDeclaredField("sCurrentActivityThread");
        currentActivityThreadField.setAccessible(true);
        Object currentActivityThread = currentActivityThreadField.get(null);
        // 1.2获取Activity的mInstrumentation并替换成我们自己的
        Field instrumentationField = clazz.getDeclaredField("mInstrumentation");
        instrumentationField.setAccessible(true);
        Instrumentation old = (Instrumentation) instrumentationField.get(currentActivityThread);
        instrumentationField.set(currentActivityThread, new HookInstrumentation(old, proxyClass));
    }

    // 2.Hook Instrumentation.execStartActivity()方法改变其Intent,让其骗过AndroidManifest的检测
    //因为这个方法是被隐藏的所以没办法重写，只能自己动手写。
    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options)  {
        //替换Intent的ComponentName
        hookIntent(intent);

        try {
            Method method = Instrumentation.class.getDeclaredMethod("execStartActivity", Context.class, IBinder.class, IBinder.class,
                    Activity.class, Intent.class, int.class, Bundle.class);
            return (ActivityResult) method.invoke(mOldInstrumentation, who, contextThread, token, target, intent, requestCode, options);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG",e.toString());
        }
        return null;
    }

    /**
     * 替换Intent的Component
     */
    private void hookIntent(Intent intent) {
        ComponentName oldComponent = intent.getComponent();
        int index = proxyClassName.lastIndexOf(".");
        String packageName = proxyClassName.substring(0, index);
        Log.d("TAG", packageName + "---" + proxyClassName);
        ComponentName proxyComponent = new ComponentName(packageName, proxyClassName);
        intent.setComponent(proxyComponent);
        intent.putExtra(KEY_ORIGINAL_Component, oldComponent);
    }


    // 3.Hook Instrumentation.newActivity(ClassLoader cl, String className,Intent intent) 还原原来Intent
    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ComponentName oldComponent = intent.getParcelableExtra(KEY_ORIGINAL_Component);
        if (oldComponent != null){
            intent.removeExtra(KEY_ORIGINAL_Component);
            intent.setComponent(oldComponent);
            className = oldComponent.getClassName();
        }
        return super.newActivity(cl, className, intent);
    }
}
