package com.east.resourceplugin

import android.content.Context
import android.content.res.TypedArray
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewParent
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatViewInflater
import androidx.core.view.LayoutInflaterCompat
import androidx.core.view.ViewCompat
import com.east.resourceplugin.skin.CompatViewInflater
import com.east.resourceplugin.skin.SkinManager
import com.east.resourceplugin.skin.config.SkinSPUtils
import com.east.resourceplugin.skin.support.SkinSupport
import org.xmlpull.v1.XmlPullParser

/**
 * |---------------------------------------------------------------------------------------------------------------|
 *  @description: 存在换肤需求的Activity基类
 *  @author: jamin
 *  @date: 2020/4/30
 * |---------------------------------------------------------------------------------------------------------------|
 */
abstract class BaseSkinActivity : AppCompatActivity(), LayoutInflater.Factory2 {

    var mAppCompatViewInflater: CompatViewInflater? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        //这个layoutInflater是单例如果为它设置了工厂，则AppCompatActivity设置的工厂就无效了。
        val layoutInflater = LayoutInflater.from(this)
        LayoutInflaterCompat.setFactory2(layoutInflater, this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        parent: View?,
        name: String,
        context: Context,
        attrs: AttributeSet
    ): View? {
        // 1.创建View
        var view = createView(parent, name, context, attrs)

        // 2.拦截并解析属性(background,textColor,src等)
        val skinView = SkinSupport.getSkinView(view, context, attrs)

        // 3.交由SkinManager统一管理
        if (skinView != null){
            SkinManager.getInstance().saveSkinView(this, skinView)
            // 4.保存并沿用当前皮肤状态
            if(TextUtils.isEmpty(SkinSPUtils.getInstance().skinPath))
                skinView.skin(true)
            else
                skinView.skin(false)
        }

        return view
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return onCreateView(null, name, context, attrs)
    }

    private fun createView(
        parent: View?, name: String?, context: Context,
        attrs: AttributeSet
    ): View? {
        if (mAppCompatViewInflater == null) {
            val a: TypedArray = context.obtainStyledAttributes(R.styleable.AppCompatTheme)
            val viewInflaterClassName =
                a.getString(R.styleable.AppCompatTheme_viewInflaterClass)
            if (viewInflaterClassName == null
                || AppCompatViewInflater::class.java.name == viewInflaterClassName
            ) {
                // Either default class name or set explicitly to null. In both cases
                // create the base inflater (no reflection)
                mAppCompatViewInflater = CompatViewInflater()
            } else {
                try {
                    val viewInflaterClass =
                        Class.forName(viewInflaterClassName)
                    mAppCompatViewInflater = viewInflaterClass.getDeclaredConstructor()
                        .newInstance() as CompatViewInflater
                } catch (t: Throwable) {
                    Log.i(
                        "TAG", "Failed to instantiate custom view inflater "
                                + viewInflaterClassName + ". Falling back to default.", t
                    )
                    mAppCompatViewInflater = CompatViewInflater()
                }
            }
        }
        var inheritContext = false
        if (Build.VERSION.SDK_INT < 21) {
            inheritContext =
                if (attrs is XmlPullParser // If we have a XmlPullParser, we can detect where we are in the layout
                ) (attrs as XmlPullParser).depth > 1 // Otherwise we have to use the old heuristic
                else shouldInheritContext(parent as ViewParent)
        }
        return mAppCompatViewInflater!!.createView(
            parent, name, context, attrs, inheritContext,
            Build.VERSION.SDK_INT < 21,  /* Only read android:theme pre-L (L+ handles this anyway) */
            true,  /* Read read app:theme as a fallback at all times for legacy reasons */
            false /* Only tint wrap the context if enabled */
        )
    }


    private fun shouldInheritContext(parent: ViewParent): Boolean {
        var parent: ViewParent? = parent
            ?: // The initial parent is null so just return false
            return false
        val windowDecor: View = window.decorView
        while (true) {
            if (parent == null) {
                // Bingo. We've hit a view which has a null parent before being terminated from
                // the loop. This is (most probably) because it's the root view in an inflation
                // call, therefore we should inherit. This works as the inflated layout is only
                // added to the hierarchy at the end of the inflate() call.
                return true
            } else if (parent === windowDecor || parent !is View
                || ViewCompat.isAttachedToWindow((parent as View?)!!)
            ) {
                // We have either hit the window's decor view, a parent which isn't a View
                // (i.e. ViewRootImpl), or an attached view, so we know that the original parent
                // is currently added to the view hierarchy. This means that it has not be
                // inflated in the current inflate() call and we should not inherit the context.
                return false
            }
            parent = parent.getParent()
        }
    }

    override fun onDestroy() {
        //销毁SkinManager对Activity的引用
        SkinManager.getInstance().destroyActivitySkinView(this)
        super.onDestroy()

    }
}