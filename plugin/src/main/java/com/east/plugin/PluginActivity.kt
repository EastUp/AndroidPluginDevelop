package com.east.plugin

import android.os.Bundle
import android.widget.Toast
import com.east.plugin.R
import com.east.androidplugindevelop.activity.ainterface.iplugin.BasePluginActivity
import kotlinx.android.synthetic.main.activity_main.*

class PluginActivity : BasePluginActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val textView = TextView(this)
//        textView.text = "aaa"
//        setContentView(textView)
        setContentView(R.layout.activity_main)

        iv1.setImageResource(R.drawable.splash_slogan)
        iv2.setImageResource(R.drawable.splash_picture)

        iv1.setOnClickListener {
            Toast.makeText(this,"点击了我",Toast.LENGTH_SHORT).show()
        }
    }
}