package com.east.androidplugindevelop.activity

import android.app.Activity
import android.content.res.AssetManager
import android.content.res.Resources
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.east.androidplugindevelop.activity.hook.Reflect
import dalvik.system.DexClassLoader
import java.io.File

open class StubBaseActivity : AppCompatActivity() {

    protected var activityClassLoader: ClassLoader? = null
    protected var activityName = ""
    private var pluginPath = ""
    private var pluginAssetManager: AssetManager? = null
    private var pluginResources: Resources? = null
    private var pluginTheme: Resources.Theme? = null
    private var nativeLibDir: String? = null
    private var dexOutPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        nativeLibDir = File(filesDir, "pluginlib").absolutePath
        dexOutPath = File(filesDir, "dexout").absolutePath
        pluginPath = intent.getStringExtra("pluginPath")
        activityName = intent.getStringExtra("activityName")
        activityClassLoader = DexClassLoader(pluginPath, dexOutPath, nativeLibDir, this::class.java.classLoader)
        handleResources()
    }

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
    }

    override fun getResources(): Resources? {
        return pluginResources ?: super.getResources()
    }

    override fun getAssets(): AssetManager {
        return pluginAssetManager ?: super.getAssets()
    }

    override fun getClassLoader(): ClassLoader {
        return activityClassLoader ?: super.getClassLoader()
    }

    override fun getTheme(): Resources.Theme {
        return pluginTheme ?:super.getTheme()
    }

    private fun handleResources() {
        try {
            pluginAssetManager = AssetManager::class.java.newInstance()
            val addAssetPathMethod = pluginAssetManager?.javaClass?.getMethod("addAssetPath", String::class.java)
            addAssetPathMethod?.invoke(pluginAssetManager, pluginPath)
        } catch (e: Exception) {
        }
        pluginResources = Resources(pluginAssetManager, super.getResources().displayMetrics, super.getResources().configuration)
        pluginTheme = pluginResources?.newTheme()
        pluginTheme?.setTo(super.getTheme())
        var reflect = Reflect.on(this)
        val themeResource: Int = reflect.get("mThemeResource")
        pluginTheme!!.applyStyle(themeResource, true)
    }
}
