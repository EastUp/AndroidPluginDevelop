package com.east.androidplugindevelop

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
/**
 * |---------------------------------------------------------------------------------------------------------------|
 *  @description:  占坑的Activity，用于在Instrumentation.execStartActivity时替换,newActivity的时候再把原来的Intent还原
 *  @author: jamin
 *  @date: 2020/6/3 10:24
 * |---------------------------------------------------------------------------------------------------------------|
 */
class ProxyActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_proxy)
    }
}