package com.east.androidplugindevelop

import android.app.Application
import com.east.androidplugindevelop.activity.hook.dex.LoadPluginDexManager
import com.east.androidplugindevelop.activity.hook.HookInstrumentation
import com.east.androidplugindevelop.activity.hook.PluginContext
import com.east.androidplugindevelop.contentprovider.PluginUtils
import dalvik.system.DexClassLoader
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
        init()
    }

    /**
     *  加载插件
     */
    private fun init() {
        extractPlugin()

        var pluginPath = File(filesDir.absolutePath, "plugin.apk").absolutePath

        //这步不需要，直接在hookInstrumentation中用pluginClassloader生成插件类，不然会起冲突。
//        var loadPluginDexManager = LoadPluginDexManager(this)
//        loadPluginDexManager.loadPlugin(pluginPath) //加载插件的类到本地ClassLoader

        var nativeLibDir = File(filesDir, "pluginlib")
        var dexOutPath = File(filesDir, "dexout")
        if (!dexOutPath.exists()) {
            dexOutPath.mkdirs()
        }
        var pluginClassLoader = DexClassLoader(pluginPath, dexOutPath.absolutePath, nativeLibDir.absolutePath, this::class.java.classLoader)
        PluginUtils.classLoader = pluginClassLoader
        HookInstrumentation.hook(this,
            PluginContext(
                pluginPath,
                this,
                this,
                pluginClassLoader
            )
        )
    }

    private fun extractPlugin() {
        var inputStream = assets.open("plugin.apk")
        File(filesDir.absolutePath, "plugin.apk").writeBytes(inputStream.readBytes())
    }

}