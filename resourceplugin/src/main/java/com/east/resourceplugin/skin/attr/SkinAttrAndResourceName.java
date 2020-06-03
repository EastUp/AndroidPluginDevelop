package com.east.resourceplugin.skin.attr;

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description: 保存attrName和resourceName的实体,一个SkinView可能会同时有多个SkinAttrAndResourceName
 *  @author: jamin
 *  @date: 2020/5/21
 * |---------------------------------------------------------------------------------------------------------------|
 */
public class SkinAttrAndResourceName {
    //属性的名称:background/src/textColor之类的
    private String mAttributeName;

    //资源文件的名称
    private String mResourceName;

    public SkinAttrAndResourceName(String mAttributeName, String mResourceName) {
        this.mAttributeName = mAttributeName;
        this.mResourceName = mResourceName;
    }

    public String getAttributeName() {
        return mAttributeName;
    }

    public void setAttributeName(String mAttributeName) {
        this.mAttributeName = mAttributeName;
    }

    public String getResourceName() {
        return mResourceName;
    }

    public void setResourceName(String mResourceName) {
        this.mResourceName = mResourceName;
    }
}
