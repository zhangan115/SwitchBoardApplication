package com.board.applicion.view.deploy

import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import com.board.applicion.R
import com.board.applicion.base.BaseActivity
import com.board.applicion.mode.DatabaseStore
import io.objectbox.query.QueryBuilder
import kotlinx.android.synthetic.main.activity_search.*

abstract class BaseSearchActivity<T> : BaseActivity(){

    open lateinit var databaseStore: DatabaseStore<T>

    open var searchContentStr = ""

    abstract fun getDataClass(): Class<T>

    abstract fun getQueryBuild(): QueryBuilder<T>

    open val datas = ArrayList<T>()

    override fun initView(savedInstanceState: Bundle?) {
        setDarkStatusIcon(true)
        searchView.addTextChangedListener(object :TextWatcher{

            override fun afterTextChanged(s: Editable?) {
                if (TextUtils.isEmpty(s.toString())) {
                    cleanData()
                } else {
                    searchContentStr = s!!.toString()
                    databaseStore.getQueryData(getQueryBuild().build()) {
                        if (it.isEmpty()) {
                            cleanData()
                            return@getQueryData
                        }
                        datas.clear()
                        datas.addAll(it)
                        recycleView.adapter?.notifyDataSetChanged()
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }
        })
        searchView.hint = getHitStr()
        recycleView.layoutManager = GridLayoutManager(this, 1)
        setSearchAdapter()
        cancelButton.setOnClickListener {
            this.finish()
        }
    }

    override fun initData() {
        databaseStore = DatabaseStore(lifecycle, getDataClass())
    }

    override fun getContentView(): Int {
        return R.layout.activity_search
    }

    abstract fun getHitStr(): String

    abstract fun setSearchAdapter()

    private fun cleanData() {
        datas.clear()
        recycleView.adapter?.notifyDataSetChanged()
    }

}