package com.east.resourceplugin.skin.support;

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description: 换肤需要替换资源的Attr属性
 *  @author: jamin
 *  @date: 2020/5/22
 * |---------------------------------------------------------------------------------------------------------------|
 */
public enum  ChangeSkinAttrs {
    BACKGROUND("background"),
    SRC("src"),
    TEXTCOLOR("textColor");

    public String attrName;

    ChangeSkinAttrs(String attrName) {
        this.attrName = attrName;
    }

}
