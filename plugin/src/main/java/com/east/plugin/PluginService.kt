package com.east.plugin

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class PluginService : Service() {

    override fun onCreate() {
        Log.d("TAG","plugin service onCreate")
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("TAG","plugin service onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d("TAG","plugin service onDestory")
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

}