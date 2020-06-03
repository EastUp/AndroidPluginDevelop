package com.east.resourceplugin.skin.attr;

import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


import com.east.resourceplugin.skin.ResourcesManager;
import com.east.resourceplugin.skin.support.ChangeSkinAttrs;

import java.util.List;

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description: 需要换肤的View实体
 *  @author: jamin
 *  @date: 2020/5/21
 * |---------------------------------------------------------------------------------------------------------------|
 */
public class SkinView {
    private View view;

    private List<SkinAttrAndResourceName> skinAttrAndResourceNames;

    public SkinView(View view, List<SkinAttrAndResourceName> skinAttrAndResourceNames) {
        this.view = view;
        this.skinAttrAndResourceNames = skinAttrAndResourceNames;
    }

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public List<SkinAttrAndResourceName> getSkinAttrAndResourceNames() {
        return skinAttrAndResourceNames;
    }

    public void setSkinAttrAndResourceNames(List<SkinAttrAndResourceName> skinAttrAndResourceNames) {
        this.skinAttrAndResourceNames = skinAttrAndResourceNames;
    }

    /**
     * @param original 是否换回系统原来的皮肤
     */
    public void skin(boolean original){
        for (SkinAttrAndResourceName skinAttrAndResourceName : skinAttrAndResourceNames) {
            String name = skinAttrAndResourceName.getAttributeName();
            String resourceName = skinAttrAndResourceName.getResourceName();
            //根据不同的属性名称为view设置不同的资源
            if(name.equals(ChangeSkinAttrs.TEXTCOLOR.attrName)){
                ColorStateList colors = ResourcesManager.getInstance().getColorByName(resourceName, original);
                if(colors==null){
                    return;
                }
                if(view instanceof TextView){
                    TextView textView = (TextView) view;
                    textView.setTextColor(colors);
                }
            }else if(name.equals(ChangeSkinAttrs.BACKGROUND.attrName)){
                //背景有可能是图片
                Drawable drawable = ResourcesManager.getInstance().getDrawableByName(resourceName, original);
                if(drawable != null){
                    view.setBackground(drawable);
                }
                //也有可能是颜色
                ColorStateList colors = ResourcesManager.getInstance().getColorByName(resourceName, original);
                if(colors==null){
                    return;
                }
                view.setBackgroundColor(colors.getDefaultColor());
            }else if(name.equals(ChangeSkinAttrs.SRC.attrName)){
                Drawable drawable = ResourcesManager.getInstance().getDrawableByName(resourceName, original);
                if(drawable == null)
                    return;
                if(view instanceof ImageView){
                    ImageView imageView = (ImageView) view;
                    imageView.setImageDrawable(drawable);
                }
            }
        }
    }
}
