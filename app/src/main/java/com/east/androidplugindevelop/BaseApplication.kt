package com.east.androidplugindevelop

import android.app.Application
import com.east.androidplugindevelop.hook.HookInstrumentation
import java.io.File

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