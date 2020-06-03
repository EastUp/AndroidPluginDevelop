package com.east.androidplugindevelop

import android.app.Application

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *  @description:
 *  @author: jamin
 *  @date: 2020/6/3
 * |---------------------------------------------------------------------------------------------------------------|
 */
class BaseApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        HookInstrumentation.hook(ProxyActivity::class.java)
    }
}