package com.east.androidplugindevelop.activity.plugin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.east.androidplugindevelop.R
import kotlinx.android.synthetic.main.activity_test.*

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

        tv_info.text = intent.getStringExtra("info")
    }
}