package com.east.androidplugindevelop.hook;

import android.app.Activity;
import android.app.Application;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.lang.ref.WeakReference;
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

    private Instrumentation mOldInstrumentation;
    private String proxyClassName; // 坑位Activity的全路径名

    public HookInstrumentation(Instrumentation old, Class proxyClass) {
        this.mOldInstrumentation = old;
        proxyClassName = proxyClass.getCanonicalName();
        Log.d("TAG", proxyClassName);
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
        int index = proxyClassName.lastIndexOf(".");
        String packageName = proxyClassName.substring(0, index);
        Log.d("TAG", packageName + "---" + proxyClassName);
        ComponentName proxyComponent = new ComponentName(packageName, proxyClassName);
        if (!oldComponent.getPackageName().equals(packageName)
                || oldComponent.getClassName().equals("com.east.androidplugindevelop.plugin.NotRegisterActivity")) {
            intent.setComponent(proxyComponent);
            intent.putExtra(KEY_ORIGINAL_Component, oldComponent);
        }
    }


    // 3.Hook Instrumentation.newActivity(ClassLoader cl, String className,Intent intent) 还原原来Intent
    @Override
    public Activity newActivity(ClassLoader cl, String className, Intent intent) throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        ComponentName oldComponent = intent.getParcelableExtra(KEY_ORIGINAL_Component);
        if (oldComponent != null) {
            intent.setComponent(oldComponent);
            className = oldComponent.getClassName();
        }
        Activity newActivity = super.newActivity(cl, className, intent);

        return newActivity;
    }


    // 4.替换资源
    @Override
    public void callActivityOnCreate(Activity activity, Bundle icicle) {
        Intent intent = activity.getIntent();
        ComponentName oldComponent = intent.getParcelableExtra(KEY_ORIGINAL_Component);
        if (oldComponent != null) {
            // 在这里进行资源替换
            injectActivityReources(activity);
        }
        super.callActivityOnCreate(activity, icicle);
    }

    //替换资源
    private void injectActivityReources(Activity activity) {
        try {
            //通过反射实例化AssetManager
            AssetManager assetManager = AssetManager.class.newInstance();
            //加载额外的资源
            Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            //反射执行方法
            addAssetPath.invoke(assetManager, new File(activity.getCacheDir(), "plugin.apk").getAbsolutePath());
            //这是插件资源的Resource
            Resources resources = new Resources(assetManager, new DisplayMetrics(), new Configuration());

            //1.把Activity的资源替换成插件的资源
            Context baseContext = activity.getBaseContext();//获取到的是ContextImpl
            Field mResourcesField = baseContext.getClass().getDeclaredField("mResources");
            mResourcesField.setAccessible(true);
            mResourcesField.set(baseContext, resources);
            //将ContextImpl重新设置回去
            Field baseContextField = ContextWrapper.class.getDeclaredField("mBase");
            baseContextField.setAccessible(true);
            baseContextField.set(activity, baseContext);

            //替换context中mPackageInfo中的Resources对象
            Field field = null;
            field = baseContext.getClass().getDeclaredField("mPackageInfo");
            field.setAccessible(true);
            Object packageInfo = field.get(baseContext);
            field = PackageInfo.class.getDeclaredField("mResources");
            field.setAccessible(true);
            field.set(packageInfo, resources);
//            //将新的resources替换掉ResourcesManager中mActiveResources对Resources的引用
//            Class<?> resourcesClass = Class.forName("android.app.ResourcesManager");
//            Method method = resourcesClass.getDeclaredMethod("getInstance");
//            method.setAccessible(true);
//            Object resourcesManager = method.invoke(null);
//            field = resourcesClass.getDeclaredField("mResourceImpls");
//            field.setAccessible(true);
//            ArrayMap<ResourcesKey, WeakReference<Resources>> map = field.get(resourcesManager)
//                    val key = map.keys.iterator().next()
//            map[key] = WeakReference(resources)

//            //2. 把Application的资源替换成插件资源
//            //2.1 获取Application的ContextImpl
//            Field mContextField = ContextWrapper.class.getDeclaredField("mBase");
//            mContextField.setAccessible(true);
//            Object o = mContextField.get(activity.getApplication());//ContextImpl
//            //2.2 获取ContextImpl的mResources
//            Class<?> contextImplClazz = Class.forName("android.app.ContextImpl");
//            Field mAppResources = contextImplClazz.getDeclaredField("mResources");
//            mAppResources.setAccessible(true);
//            //2.3 设置ContextImpl的Resource为插件的Resources
//            mAppResources.set(o, resources); //设置Resource进去
//            //2.4 将ContextImpl重新设置给Application
//            mContextField.set(activity.getApplication(), o);
//
//
//            // for native activity
//            Intent intent = activity.getIntent();
//            ComponentName componentName = intent.getParcelableExtra(KEY_ORIGINAL_Component);
//            Intent wrapperIntent = new Intent(intent);
//            wrapperIntent.setClassName(componentName.getPackageName(), componentName.getClassName());
//            activity.setIntent(wrapperIntent);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG", e.toString());
        }
    }
}
