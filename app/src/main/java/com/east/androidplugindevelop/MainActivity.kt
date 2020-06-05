package com.east.androidplugindevelop

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.east.androidplugindevelop.activity.ainterface.StubInterfaceActivity
import com.east.androidplugindevelop.activity.plugin.NotRegisterActivity
import com.east.androidplugindevelop.activity.reflect.StubReflectActivity
import com.east.androidplugindevelop.broadcast.BroadcastUtils
import com.east.androidplugindevelop.contentprovider.PluginUtils
import com.east.androidplugindevelop.service.StubService
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var pluginPath: String
    private lateinit var activityName: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        pluginPath = File(filesDir.absolutePath, "plugin.apk").absolutePath
        activityName = "com.east.plugin.PluginActivity"
    }

    fun onClick(v: View){
        when(v){
            //跳转到本地没在AndroidManifest注册的Activity中
            startLocalActivity ->{
                startActivity(Intent(this,NotRegisterActivity::class.java).apply {
                    putExtra("info","传递过来的消息--MainActivity")
                })
            }
            //打开插件中的Activity By Hook
            startPluginActivityByHook -> {
                var intent = Intent().apply {
                    component = ComponentName("com.east.plugin",activityName)
                }
                startActivity(intent)
            }
            //打开插件中的Activity By Interface
            startPluginActivityByInterface -> {
                StubInterfaceActivity.startPluginActivity(this, pluginPath, activityName)
            }

            //打开插件中的Activity By Reflect
            startPluginActivityByReflect -> {
                StubReflectActivity.startPluginActivity(this, pluginPath, activityName)
            }

            //启动插件中服务
            startPluginService -> {
                StubService.startService(this, PluginUtils.classLoader!!, "com.east.plugin.PluginService")
            }

            //注册插件广播
            registerPluginBroadcast -> {
                BroadcastUtils.registerBroadcastReceiver(this, PluginUtils.classLoader!!, "test_plugin_broadcast", "com.east.plugin.PluginBroadcastReceiver")
            }

            //取消注册插件广播
            unregisterPluginBroadcast -> {
                BroadcastUtils.unregisterBroadcastReceiver(this, "test_plugin_broadcast")
            }

            //发送插件广播
            sendPluginBroadcast -> {
                val intent = Intent()
                intent.action = "test_plugin_broadcast"
                sendBroadcast(intent)
            }

            queryContentProvider ->{
                val uri = Uri.parse("content://com.east.stubprovider/plugin1")
                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor?.moveToFirst()
                val res = cursor?.getString(0)
                Log.d("TAG","provider query res: $res")
            }

            queryContentProvider2 ->  {
                val uri = Uri.parse("content://com.east.stubprovider/plugin2")
                val cursor = contentResolver.query(uri, null, null, null, null)
                cursor?.moveToFirst()
                val res = cursor?.getString(0)
                Log.d("TAG","provider query res: $res")
            }


        }
    }



}