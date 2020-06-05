package com.east.androidplugindevelop.activity.hook;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;

import java.lang.reflect.Method;

/**
 * Created by renyugang on 16/8/12.
 * 这个类直接拷贝 DynamicApk 里的实现
 */
public class PluginContext extends ContextWrapper {

    private Context context;
    private Application application;
    private ClassLoader classLoader;
    private Resources resources;
    private AssetManager assetManager;
    private Resources.Theme theme;
    private String pluginPath;

    public PluginContext(String pluginPath, Context context, Application application, ClassLoader classLoader) {
        super(context);
        this.context = context;
        this.application = application;
        this.pluginPath = pluginPath;
        this.classLoader = classLoader;
        generateResources();
    }

    private void generateResources() {
        try {
            assetManager = AssetManager.class.newInstance();
            Method method = assetManager.getClass().getMethod("addAssetPath", String.class);
            method.invoke(assetManager, pluginPath);
            resources = new Resources(assetManager, context.getResources().getDisplayMetrics(), context.getResources().getConfiguration());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Context getApplicationContext() {
        return application;
    }

    private Context getHostContext() {
        return getBaseContext();
    }

    @Override
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    @Override
    public PackageManager getPackageManager() {
        return context.getPackageManager();
    }

    @Override
    public Resources getResources() {
        return resources;
    }

    @Override
    public AssetManager getAssets() {
        return assetManager;
    }

    @Override
    public Resources.Theme getTheme() {
        return theme;
    }
}
