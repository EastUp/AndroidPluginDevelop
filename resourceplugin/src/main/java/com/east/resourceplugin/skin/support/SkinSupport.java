package com.east.resourceplugin.skin.support;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.east.resourceplugin.skin.SkinManager;
import com.east.resourceplugin.skin.attr.SkinAttrAndResourceName;
import com.east.resourceplugin.skin.attr.SkinView;
import com.east.resourceplugin.skin.callback.JudgeViewAttributeNameListener;

import java.util.ArrayList;
import java.util.List;

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description:
 *  @author: jamin
 *  @date: 2020/5/21
 * |---------------------------------------------------------------------------------------------------------------|
 */
public class SkinSupport {

    public final static String TAG = "TAG-SkinSupport";

    /**
     * 获取到SkinView
     */
    public static SkinView getSkinView(View view, Context context, AttributeSet attrs){
        List<SkinAttrAndResourceName> skinAttrAndResourceNameList = new ArrayList<>();

        int attributeCount = attrs.getAttributeCount();
        for(int i=0;i< attributeCount;i++){
            String attributeName = attrs.getAttributeName(i);
            String attributeValue = attrs.getAttributeValue(i);//全是id
            Log.d(TAG,attributeName+"---->"+attributeValue);
            //如果是自己需要的attr则加入到skinAttrs中
            if(judgeViewAttributeName(attributeName)){
                String resourceName = getReourceName(context,attributeValue);
                if(TextUtils.isEmpty(resourceName))
                    continue;
                SkinAttrAndResourceName skinAttrAndResourceName = new SkinAttrAndResourceName(attributeName,resourceName);
                skinAttrAndResourceNameList.add(skinAttrAndResourceName);
            }
        }
        if(skinAttrAndResourceNameList.size()>0)
            return new SkinView(view,skinAttrAndResourceNameList);
        else
            return null;
    }


    /**
     * 获取资源的名称
     */
    private static String getReourceName(Context context, String attributeValue) {

        if(attributeValue.startsWith("@")){
            attributeValue = attributeValue.substring(1);
            int resId = Integer.parseInt(attributeValue);
            return context.getResources().getResourceEntryName(resId);
        }

        return null;
    }


    /**
     *  判断是不是需要换肤的View
     */
    private static boolean judgeViewAttributeName(String attributeName){
        JudgeViewAttributeNameListener judgeViewAttributeNameListener = SkinManager.getInstance().getJudgeViewAttributeNameListener();
        if(judgeViewAttributeNameListener !=null)
            return judgeViewAttributeNameListener.judgeViewAttributeName(attributeName);
        else
            throw new RuntimeException("请设置SkinManager.getInstance().setJudgeViewAttributeNameListener拦截属性");

//        return TextUtils.equals(attributeName,ChangeSkinAttrs.BACKGROUND.attrName) ||
//                TextUtils.equals(attributeName,ChangeSkinAttrs.TEXTCOLOR.attrName) ||
//                TextUtils.equals(attributeName,ChangeSkinAttrs.SRC.attrName);
    }


}
