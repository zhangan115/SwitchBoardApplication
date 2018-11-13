package com.board.applicion.base

import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.TextView
import com.board.applicion.R
import com.library.widget.TextViewVertical


abstract class BaseActivity : AppCompatActivity() {

    open val showToolbar = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getSaveState(savedInstanceState)
        initThem()
        setContentView(getContentView())
        initToolBar()
        initData()
        initView(savedInstanceState)
    }

    /**
     * 初始化view
     */
    abstract fun initView(savedInstanceState: Bundle?)

    /**
     * 初始化数据
     */
    abstract fun initData()


    open fun initThem() {
        //设置主题
    }

    open fun getSaveState(savedInstanceState: Bundle?) {

    }

    /**
     * 初始化toolbar
     */
    open fun initToolBar() {
        val toolBar = findViewById<Toolbar>(R.id.toolbar) ?: return
        if (!showToolbar) {
            return
        }
        val titleTv = findViewById<TextView>(R.id.titleId)
        titleTv.text = getToolBarTitle()
        setSupportActionBar(toolBar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
        }
        toolBar.setNavigationOnClickListener {
            onBackAction()
        }
    }

    /**
     * 获取界面布局
     */
    abstract fun getContentView(): Int

    /**
     * 获取toolbar的标题
     */
    @Nullable
    open fun getToolBarTitle(): String? {
        return null
    }

    override fun onBackPressed() {
        onBackAction()
    }

    /**
     * 按下返回键或者toolbar的返回键
     */
    open fun onBackAction() {
        super.onBackPressed()
    }

    /**
     * 状态栏完全透明
     */
    fun transparentStatusBar() {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
    }

    /**
     * 修改状态栏icon 颜色
     *
     * @param bDark 是否将icon 颜色变为灰色
     */
    fun setDarkStatusIcon(bDark: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val decorView = window.decorView
            if (decorView != null) {
                var vis = decorView.systemUiVisibility
                vis = if (bDark) {
                    vis or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    vis and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
                }
                decorView.systemUiVisibility = vis
            }
        }
    }

    /**
     * 查找颜色
     */
    fun findColor(color: Int): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            resources.getColor(color, theme)
        } else {
            resources.getColor(color)
        }
    }

    /**
     * 查找字符串
     */
    fun findString(str: Int): String {
        return resources.getString(str, theme)
    }

    /**
     * 查找图片
     */
    fun findDrawable(drawable: Int): Drawable {
        return resources.getDrawable(drawable, theme)
    }

}
