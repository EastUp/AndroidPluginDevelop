package com.east.resourceplugin.skin.callback;


import com.east.resourceplugin.skin.attr.SkinView;

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description:   换肤时的监听
 *  @author: jamin
 *  @date: 2020/5/22
 * |---------------------------------------------------------------------------------------------------------------|
 */
public interface SkinChangeListener {
    void changeSkin(boolean original, SkinView skinView);
}
