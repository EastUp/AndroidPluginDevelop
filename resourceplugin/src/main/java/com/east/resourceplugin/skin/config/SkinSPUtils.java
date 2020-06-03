package com.east.resourceplugin.skin.config;

import android.content.Context;

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description: Skin的SP工具
 *  @author: jamin
 *  @date: 2020/5/22
 * |---------------------------------------------------------------------------------------------------------------|
 */
public class SkinSPUtils {

    private Context mContext;

    private volatile static SkinSPUtils skinSPUtils;

    private SkinSPUtils(){

    }

    public static SkinSPUtils getInstance(){
        if(skinSPUtils == null){
            synchronized (SkinSPUtils.class){
                if(skinSPUtils == null){
                    skinSPUtils = new SkinSPUtils();
                }
            }
        }
        return skinSPUtils;
    }

    public void init(Context context){
        this.mContext = context.getApplicationContext();
    }

    /**
     *  保存皮肤路径到SP中
     */
    public void saveSkinPath(String path){
        mContext.getSharedPreferences(SkinConfig.SKIN_INFO_NAME,Context.MODE_PRIVATE)
                .edit().putString(SkinConfig.SKIN_PATH,path).commit();
    }

    /**
     *  获取皮肤路径
     */
    public String getSkinPath(){
        return mContext.getSharedPreferences(SkinConfig.SKIN_INFO_NAME,Context.MODE_PRIVATE)
                .getString(SkinConfig.SKIN_PATH,"");
    }

    /**
     *  清空SkinPath数据
     */
    public void clearSkinPath(){
        saveSkinPath("");
    }



}
