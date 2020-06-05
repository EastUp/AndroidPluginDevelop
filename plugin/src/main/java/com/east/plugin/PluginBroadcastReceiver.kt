package com.east.plugin

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class PluginBroadcastReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("TAG","receive broadcast in plugin")
    }
}