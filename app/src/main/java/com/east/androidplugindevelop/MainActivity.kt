package com.east.androidplugindevelop

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.east.androidplugindevelop.plugin.NotRegisterActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun onClick(v: View){
        startActivity(Intent(this,NotRegisterActivity::class.java).apply {
            putExtra("info","传递过来的消息--MainActivity")
        })
    }
}