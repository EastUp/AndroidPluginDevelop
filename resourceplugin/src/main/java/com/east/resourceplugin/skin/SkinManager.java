package com.east.resourceplugin.skin;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import androidx.collection.ArrayMap;


import com.east.resourceplugin.skin.attr.SkinView;
import com.east.resourceplugin.skin.callback.JudgeViewAttributeNameListener;
import com.east.resourceplugin.skin.callback.SkinChangeListener;
import com.east.resourceplugin.skin.config.SkinSPUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 * @description: 换肤管理类
 * @author: jamin
 * @date: 2020/5/21
 * |---------------------------------------------------------------------------------------------------------------|
 */
public class SkinManager {

    private Context context;
    //用来影视Activity和其中需要换肤的View
    private ArrayMap<Activity, List<SkinView>> mSkinMap = new ArrayMap<>();

    private SkinChangeListener mSkinChangeListener; //换肤的回调，可以写自己的逻辑

    private JudgeViewAttributeNameListener mJudgeViewAttributeNameListener; //需要换肤的需要用到属性

    private volatile static SkinManager skinManager;

    public static SkinManager getInstance() {
        if (skinManager == null) {
            synchronized (SkinManager.class) {
                if (skinManager == null)
                    skinManager = new SkinManager();
            }
        }
        return skinManager;
    }

    private SkinManager() {
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        this.context = context;
        SkinSPUtils.getInstance().init(context); //初始化SkinSPUtils
        String skinPath = SkinSPUtils.getInstance().getSkinPath();
        if (!TextUtils.isEmpty(skinPath))
            ResourcesManager.getInstance().init(context,skinPath);
        else
            ResourcesManager.getInstance().init(context);
    }


    /**
     * 把需要换肤的View保存起来
     */
    public void saveSkinView(Activity activity, SkinView skinView) {
        List<SkinView> skinViewList = mSkinMap.get(activity);
        if (skinViewList == null) {
            skinViewList = new ArrayList<>();
            mSkinMap.put(activity, skinViewList);
        }
        skinViewList.add(skinView);
    }

    /**
     * 移除activity，避免造成内存泄露
     * @param activity
     */
    public void destroyActivitySkinView(@NotNull Activity activity) {
        mSkinMap.remove(activity);
    }


    /**
     * 加载资源文件
     */
    public void loadSkin(String skinPath) {

        String currentSkinPath = SkinSPUtils.getInstance().getSkinPath();
        if(TextUtils.equals(currentSkinPath,skinPath))
            return;

        //下载到的资源文件,下载到用户就没法删除的目录下
        ResourcesManager.getInstance().init(context, skinPath);
        SkinSPUtils.getInstance().saveSkinPath(skinPath);
        skin(false);
    }

    /**
     * 还原原来的资源
     */
    public void restoreDefault() {
        SkinSPUtils.getInstance().clearSkinPath();
        skin(true);
    }


    /**
     * 换肤
     *
     * @param original 是否使用原始皮肤
     */
    private void skin(boolean original) {
        for (Map.Entry<Activity, List<SkinView>> activityListEntry : mSkinMap.entrySet()) {
            List<SkinView> skinViewList = activityListEntry.getValue();
            for (SkinView skinView : skinViewList) {
//                skinView.skin(original);
                if(mSkinChangeListener!=null){
                    mSkinChangeListener.changeSkin(original,skinView);
                }

            }
        }
    }

    /**
     *  设置换肤的监听，开发者可以自己在里面写自己的换肤逻辑
     */
    public SkinManager setSkinChangeListener(SkinChangeListener skinListener){
        mSkinChangeListener = skinListener;
        return this;
    }

    public SkinManager setJudgeViewAttributeNameListener(JudgeViewAttributeNameListener judgeViewAttributeNameListener){
        mJudgeViewAttributeNameListener = judgeViewAttributeNameListener;
        return this;
    }

    public JudgeViewAttributeNameListener getJudgeViewAttributeNameListener() {
        return mJudgeViewAttributeNameListener;
    }


}
