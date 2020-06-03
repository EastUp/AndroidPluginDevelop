package com.east.resourceplugin.skin;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description: 资源管理器
 *  @author: jamin
 *  @date: 2020/5/21
 * |---------------------------------------------------------------------------------------------------------------|
 */
public class ResourcesManager {

    private Resources mSkinResource;
    private Resources mOriginalResource;

    private String skinPackageName;
    private String originalPackageName;

    private volatile static ResourcesManager resourceManager;

    public static ResourcesManager getInstance(){
        if(resourceManager == null){
            synchronized (ResourcesManager.class){
                if(resourceManager == null)
                    resourceManager = new ResourcesManager();
            }
        }
        return resourceManager;
    }

    private ResourcesManager() {
    }

    public void init(Context context,String skinPath){
        try {
            AssetManager assetManager = AssetManager.class.newInstance();
            //添加资源目录
            Method addAssetPath = AssetManager.class.getDeclaredMethod("addAssetPath",String.class);
            addAssetPath.invoke(assetManager,skinPath);
            mSkinResource = new Resources(assetManager,new DisplayMetrics(),new Configuration());
        } catch (Exception e) {
            e.printStackTrace();
            mSkinResource = null;
        }
        mOriginalResource = context.getApplicationContext().getResources();
        skinPackageName = context.getApplicationContext().getPackageManager().getPackageArchiveInfo(
            skinPath, PackageManager.GET_ACTIVITIES
        ).packageName;
        originalPackageName = context.getPackageName();
    }

    public void init(Context context){
        mOriginalResource = context.getApplicationContext().getResources();
        originalPackageName = context.getPackageName();
    }

    public void setContext(Context context){

    }

    /**
     * 获取Skin资源
     */
    public Resources getSkinResource(){
        if(mSkinResource == null)
            throw new RuntimeException("是否init,资源路径是否传入正确");
        return mSkinResource;
    }

    /**
     * 获取原来的资源
     */
    public Resources getOriginalResource(){
        return mOriginalResource;
    }

    /**
     * 通过名字获取Drawable
     * @param original 是否是原生的资源
     * @return
     */
    public Drawable getDrawableByName(String resName,boolean original){
        if(!original){
            try {
                int resId = mSkinResource.getIdentifier(resName, "drawable", skinPackageName);
                Log.e("TAG","resId -> "+resId+" mPackageName -> "+skinPackageName +" resName -> "+resName);
                Drawable drawable = mSkinResource.getDrawable(resId);
                return drawable;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }else{
            try {
                int resId = mOriginalResource.getIdentifier(resName,"drawable",originalPackageName);
                Log.e("TAG","resId -> "+resId+" mPackageName -> "+skinPackageName +" resName -> "+resName);
                Drawable drawable = mOriginalResource.getDrawable(resId);
                return drawable;
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    /**
     * 通过名字获取颜色
     * @param original 是否是原生的资源
     * @return
     */
    public ColorStateList getColorByName(String resName,boolean original){
        if(!original){
            try {
                int resId = mSkinResource.getIdentifier(resName, "color", skinPackageName);
                ColorStateList color = mSkinResource.getColorStateList(resId);
                return color;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }else{
            try {
                int resId = mOriginalResource.getIdentifier(resName, "color", originalPackageName);
                ColorStateList color = mOriginalResource.getColorStateList(resId);
                return color;
            }catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }
    }

}
