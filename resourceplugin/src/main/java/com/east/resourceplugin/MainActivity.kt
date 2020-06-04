package com.east.resourceplugin

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.east.resourceplugin.skin.SkinManager
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : BaseSkinActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun skin(view: View?) {
        // 从服务器上下载
        val copyResult = copyAssetAndWrite("red.skin") // 拷贝到本地缓存
        if(!copyResult){
            Toast.makeText(this,"没有拷贝成功,确保assets目录下有资源文件",Toast.LENGTH_SHORT).show()
            return
        }
        val SkinPath = File(cacheDir, "red.skin").absolutePath

        // 换肤
        SkinManager.getInstance().loadSkin(SkinPath)
    }

    fun skin1(view: View?) {
        // 恢复默认
        SkinManager.getInstance().restoreDefault()
    }


    fun skin2(view: View?) {
        // 跳转
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


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
