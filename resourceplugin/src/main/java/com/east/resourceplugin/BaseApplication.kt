package com.east.resourceplugin

import android.app.Application
import android.text.TextUtils
import android.widget.ImageView
import android.widget.TextView
import com.east.resourceplugin.skin.ResourcesManager
import com.east.resourceplugin.skin.SkinManager
import com.east.resourceplugin.skin.attr.SkinView
import com.east.resourceplugin.skin.support.ChangeSkinAttrs

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *  @description:
 *  @author: jamin
 *  @date: 2020/6/3
 * |---------------------------------------------------------------------------------------------------------------|
 */
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SkinManager.getInstance().init(this)

        SkinManager.getInstance()
                //设置监听换肤的哪些属性
            .setJudgeViewAttributeNameListener {
                TextUtils.equals(it, ChangeSkinAttrs.BACKGROUND.attrName) ||
                        TextUtils.equals(it, ChangeSkinAttrs.TEXTCOLOR.attrName) ||
                        TextUtils.equals(it, ChangeSkinAttrs.SRC.attrName)
            }
                //换肤
            .setSkinChangeListener { original, skinView ->
                skin(original, skinView)
            }

    }


    /**
     * @param original 是否换回系统原来的皮肤
     */
    fun skin(original: Boolean, skinView: SkinView) {
        var skinAttrAndResourceNames = skinView.skinAttrAndResourceNames;
        var view = skinView.view
        for (skinAttrAndResourceName in skinAttrAndResourceNames) {
            val name = skinAttrAndResourceName.attributeName
            val resourceName = skinAttrAndResourceName.resourceName
            //根据不同的属性名称为view设置不同的资源
            if (name == ChangeSkinAttrs.TEXTCOLOR.attrName) {
                val colors =
                    ResourcesManager.getInstance().getColorByName(resourceName, original)
                        ?: return
                if (view is TextView) {
                    val textView = view as TextView
                    textView.setTextColor(colors)
                }
            } else if (name == ChangeSkinAttrs.BACKGROUND.attrName) {
                //背景有可能是图片
                val drawable =
                    ResourcesManager.getInstance().getDrawableByName(resourceName, original)
                if (drawable != null) {
                    view.setBackground(drawable)
                }
                //也有可能是颜色
                val colors =
                    ResourcesManager.getInstance().getColorByName(resourceName, original)
                        ?: return
                view.setBackgroundColor(colors.defaultColor)
            } else if (name == ChangeSkinAttrs.SRC.attrName) {
                val drawable =
                    ResourcesManager.getInstance().getDrawableByName(resourceName, original)
                        ?: return
                if (view is ImageView) {
                    val imageView = view as ImageView
                    imageView.setImageDrawable(drawable)
                }
            }
        }
    }
}