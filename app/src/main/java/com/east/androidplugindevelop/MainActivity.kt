package com.east.androidplugindevelop

import android.content.ComponentName
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.east.androidplugindevelop.dex.LoadPluginDexManager
import com.east.androidplugindevelop.hook.PluginResourceUtil
import com.east.androidplugindevelop.plugin.NotRegisterActivity
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private var isLoadPlugin = false // 是否加载插件

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        PluginResourceUtil.getInstance().restoreOwnResources(this)
    }

    override fun onResume() {
        super.onResume()
//        PluginResourceUtil.getInstance().restoreOwnResources(this)
    }

    /**
     *  跳转到本地没在AndroidManifest注册的Activity中
     */
    fun onClick(v: View){
        startActivity(Intent(this,NotRegisterActivity::class.java).apply {
            putExtra("info","传递过来的消息--MainActivity")
        })
    }

    /**
     *  加载插件
     */
    fun loadPlugin(v:View){
        val copyResult = copyAssetAndWrite("plugin.apk") // 拷贝到本地缓存
        if(!copyResult){
            Toast.makeText(this,"没有拷贝成功,确保assets目录下有资源文件", Toast.LENGTH_SHORT).show()
            return
        }
        var loadPluginDexManager = LoadPluginDexManager(this)
        loadPluginDexManager.loadPlugin(File(cacheDir,"plugin.apk").absolutePath)
        Toast.makeText(this,"插件加载完成", Toast.LENGTH_SHORT).show()
        isLoadPlugin = true
    }

    /**
     *  打开插件中的类
     */
    fun startPluginActivity(v:View){
        if(!isLoadPlugin){
            Toast.makeText(this,"请先点击加载插件按钮", Toast.LENGTH_SHORT).show()
            return
        }
//        PluginResourceUtil.getInstance().replacePulginResources(this,File(cacheDir,"plugin.apk").absolutePath)
        var intent = Intent().apply {
            component = ComponentName("com.east.connotationjokes","com.east.connotationjokes.activity.WelcomeActivity")
        }
        startActivity(intent)
    }

    /**
     *  启动插件服务
     */
    fun startPluginService(v:View){

    }

    //拷贝Asset目录下的文件到本地缓存
    fun copyAssetAndWrite(
        fileName: String?
    ): Boolean {
        try {
            val cacheDir = cacheDir
            if (!cacheDir.exists()) {
                cacheDir.mkdirs()
            }
            val outFile = File(cacheDir, fileName)
            if (!outFile.exists()) {
                val res = outFile.createNewFile()
                if (!res) {
                    return false
                }
            } else {
                if (outFile.length() > 10) { //表示已经写入一次
                    return true
                }
            }
            val `is` = assets.open(fileName!!)
            val fos = FileOutputStream(outFile)
            val buffer = ByteArray(1024)
            var byteCount: Int
            while (`is`.read(buffer).also { byteCount = it } != -1) {
                fos.write(buffer, 0, byteCount)
            }
            fos.flush()
            `is`.close()
            fos.close()
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return false
    }
}