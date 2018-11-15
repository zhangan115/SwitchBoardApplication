package com.board.applicion.view.deploy

import android.os.Bundle
import android.widget.Button
import com.board.applicion.base.BaseActivity
import com.board.applicion.mode.DatabaseStore
import io.objectbox.query.QueryBuilder

abstract class BaseAddActivity<T> : BaseActivity() {

    //传入的值
    open var beanID: Long = 0L
    open var bean: T? = null
    open lateinit var databaseStore: DatabaseStore<T>
    //数据库类
    abstract fun getDataClass(): Class<T>
    //查询条件
    abstract fun getQueryBuild(): QueryBuilder<T>
    //将数据填入到view中
    abstract fun setDataToView()
    //获取保存button
    abstract fun getSaveButton(): Button

    override fun initData() {
        beanID = intent.getLongExtra("ID", 0L)
        databaseStore = DatabaseStore(lifecycle, getDataClass())
        if (beanID != 0L) {
            bean = getQueryBuild().build().findUnique()
        }
    }

    //判断是否可以进行保存
    abstract fun canSave(): Boolean

    override fun initView(savedInstanceState: Bundle?) {
        if (bean != null) {
            setDataToView()
        }
        getSaveButton().setOnClickListener {
            if (canSave() && bean != null&&databaseStore.saveData(bean!!)) {
                finish()
            }
        }
    }

}