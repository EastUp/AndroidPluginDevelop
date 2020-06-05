package com.east.androidplugindevelop.activity.reflect

import android.app.Activity
import android.os.Bundle
import com.east.androidplugindevelop.activity.hook.Reflect
import java.lang.reflect.Method

class ReflectActivity(activityClazz: String, activityClassLoader: ClassLoader?) {
    private var clazz: Class<Activity> = activityClassLoader?.loadClass(activityClazz) as Class<Activity>
    private var activity: Activity = clazz.newInstance()


    fun attach(proxyActivity: Activity?) {
        //需要把Window给它设置进去不然没法findViewById
        val reflect = Reflect.on(activity)
        reflect.set("mWindow",proxyActivity!!.window)
        //设置mBase 否则Toast报错
        reflect.set("mBase",proxyActivity!!.baseContext)
        getMethod("attach", Activity::class.java).invoke(activity, proxyActivity)
    }

    fun onCreate(savedInstanceState: Bundle?) {
        getMethod("onCreate", Bundle::class.java).invoke(activity, savedInstanceState)
    }

    fun onStart() {
        getMethod("onStart").invoke(activity)
    }

    fun onResume() {
        getMethod("onResume").invoke(activity)
    }

    fun onPause() {
        getMethod("onPause").invoke(activity)
    }

    fun onStop() {
        getMethod("onStop").invoke(activity)
    }

    fun onDestroy() {
        getMethod("onDestroy").invoke(activity)
    }

    fun getMethod(methodName: String, vararg params: Class<*>): Method {
        return clazz.getMethod(methodName, *params)
    }
}
