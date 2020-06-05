package com.east.plugin

import android.os.Bundle
import com.east.plugin.R
import com.east.androidplugindevelop.activity.ainterface.iplugin.BasePluginActivity

class PluginActivity : BasePluginActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val textView = TextView(this)
//        textView.text = "aaa"
//        setContentView(textView)
        setContentView(R.layout.activity_main)
    }
}