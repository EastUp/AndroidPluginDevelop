package com.east.androidplugindevelop.activity.ainterface

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.east.androidplugindevelop.activity.StubBaseActivity
import com.east.androidplugindevelop.activity.ainterface.iplugin.IPluginActivity
import com.east.androidplugindevelop.activity.hook.Reflect

class StubInterfaceActivity : StubBaseActivity() {
    private var activity: IPluginActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = activityClassLoader?.loadClass(activityName)?.newInstance() as IPluginActivity?
        activity?.attach(this)

        //需要把Window给它设置进去不然没法findViewById
        val reflect = Reflect.on(activity)
        reflect.set("mWindow",window)
        //设置mBase 否则Toast报错
        reflect.set("mBase",baseContext)
        activity?.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        activity?.onStart()
    }

    override fun onResume() {
        super.onResume()
        activity?.onResume()
    }

    override fun onPause() {
        super.onPause()
        activity?.onPause()
    }

    override fun onStop() {
        super.onStop()
        activity?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.onDestroy()
    }

    companion object {
        fun startPluginActivity(context: Context, pluginPath: String, activityName: String) {
            val intent = Intent(context, StubInterfaceActivity::class.java)
            intent.putExtra("pluginPath", pluginPath)
            intent.putExtra("activityName", activityName)
            context.startActivity(intent)
        }
    }
}