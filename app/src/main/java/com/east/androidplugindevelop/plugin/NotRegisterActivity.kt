package com.east.androidplugindevelop.plugin

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.east.androidplugindevelop.R
import kotlinx.android.synthetic.main.activity_test.*
import java.io.File

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *  @description: 没在Manifest中注册的Activity
 *  @author: jamin
 *  @date: 2020/6/3
 * |---------------------------------------------------------------------------------------------------------------|
 */
class NotRegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)


        val baseContext = baseContext
        var resources = baseContext.resources
        val packageName = applicationContext.packageManager.getPackageArchiveInfo(
            File(cacheDir,"plugin.apk").absolutePath,PackageManager.GET_ACTIVITIES
        ).packageName
        val identifier = resources.getIdentifier("main_bg_color", "color", packageName)
        val color = resources.getColor(identifier)
        tv_info.setTextColor(color)
        tv_info.text = intent.getStringExtra("info")
    }
}