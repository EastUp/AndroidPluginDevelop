package com.east.androidplugindevelop.hook;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description: 插件资源替换还原工具类
 *  @author: jamin
 *  @date: 2020/6/4
 * |---------------------------------------------------------------------------------------------------------------|
 */
public class PluginResourceUtil {

    private static volatile PluginResourceUtil mDefault;

    private PluginResourceUtil(){}

    public static PluginResourceUtil getInstance(){
        if(mDefault == null){
            synchronized (PluginResourceUtil.class){
                if(mDefault == null)
                    mDefault = new PluginResourceUtil();
            }
        }
        return mDefault;
    }


    private Resources mOriginalReources;


    /**
     *  把资源替换成插件资源
     * @param activity
     * @param pluginpath
     */
    public  void replacePulginResources(Activity activity,String pluginpath){
        mOriginalReources = activity.getResources();
        try {
            //通过反射实例化AssetManager
            AssetManager assetManager = AssetManager.class.newInstance();
            //加载额外的资源
            Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath", String.class);
            //反射执行方法
            addAssetPath.invoke(assetManager, pluginpath);
            //这是插件资源的Resource
            Resources resources = new Resources(assetManager,new DisplayMetrics(),new Configuration());

            //1.把Activity的资源替换成插件的资源
            Field mResources = ContextThemeWrapper.class.getDeclaredField("mResources");
            mResources.setAccessible(true);
            mResources.set(activity,resources);

            //2. 把Application的资源替换成插件资源
            //2.1 获取Application的ContextImpl
            Field mContextField = ContextWrapper.class.getDeclaredField("mBase");
            mContextField.setAccessible(true);
            Object o = mContextField.get(activity.getApplication());//ContextImpl
            //2.2 获取ContextImpl的mResources
            Class<?> contextImplClazz = Class.forName("android.app.ContextImpl");
            Field mAppResources = contextImplClazz.getDeclaredField("mResources");
            mAppResources.setAccessible(true);
            //2.3 设置ContextImpl的Resource为插件的Resources
            mAppResources.set(o,resources); //设置Resource进去
            //2.4 将ContextImpl重新设置给Application
            mContextField.set(activity.getApplication(),o);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG",e.toString());
        }
    }

    /**
     * 还原系统资源
     * @param activity
     */
    public  void restoreOwnResources(Activity activity){
        if(mOriginalReources == null){
            Log.e("TAG","一直都是系统原生的资源");
            return;
        }

        try {
            //1.把Activity的资源替换成插件的资源
            Field mResources = Activity.class.getDeclaredField("mResources");
            mResources.setAccessible(true);
            mResources.set(activity,mOriginalReources);

            //2. 把Application的资源替换成插件资源
            Field mAppResources = Application.class.getDeclaredField("mResources");
            mAppResources.setAccessible(true);
            mAppResources.set(activity.getApplication(),mOriginalReources);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("TAG",e.toString());
        }
    }

}
