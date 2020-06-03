package com.east.resourceplugin.skin.callback;

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *
 *  @description: 哪些属性会在换肤的时候用到
 *  @author: jamin
 *  @date: 2020/5/22
 * |---------------------------------------------------------------------------------------------------------------|
 */
public interface JudgeViewAttributeNameListener {
    boolean  judgeViewAttributeName(String attributeName);
}
