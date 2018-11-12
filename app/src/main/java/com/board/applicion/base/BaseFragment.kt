package com.board.applicion.base

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LifecycleRegistry
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

abstract class BaseFragment : Fragment(), LifecycleOwner {

    override fun getLifecycle(): Lifecycle {
        return life
    }

    private var life: LifecycleRegistry = LifecycleRegistry(this)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getContentView(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initData()
        initView()
    }

    abstract fun getContentView(): Int

    abstract fun initData()

    abstract fun initView()

    /**
     * 查找颜色
     */
    fun findColor(color: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resources.getColor(color, activity?.theme)
        } else {
            resources.getColor(color)
        }
    }

    /**
     * 查找字符串
     */
    fun findString(str: Int): String {
        return resources.getString(str, activity?.theme)
    }

    /**
     * 查找图片
     */
    fun findDrawable(drawable: Int): Drawable {
        return resources.getDrawable(drawable, activity?.theme)
    }

}
