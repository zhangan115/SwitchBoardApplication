package com.board.applicion.view.deploy

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.board.applicion.R
import com.board.applicion.base.BaseActivity
import com.board.applicion.mode.DatabaseStore
import com.library.widget.ExpendRecycleView
import io.objectbox.query.QueryBuilder
import kotlinx.android.synthetic.main.activity_base_manager.*
import kotlinx.android.synthetic.main.toolbar_include.*

abstract class BaseEditActivity<T> : BaseActivity() {

    open var chooseAllMode: Boolean = false
    open var isEditMode: Boolean = false
    open lateinit var databaseStore: DatabaseStore<T>
    open var editData = ArrayList<Boolean>()
    open val data = ArrayList<T>()

    fun getRecycleView(): ExpendRecycleView {
        return findViewById(R.id.recycleView)
    }

    private fun getBottomModeLayout(): RelativeLayout {
        return deleteActionLayout
    }

    private fun getAddView(): TextView {
        return addMode
    }

    private fun getEditView(): TextView {
        return editMode
    }

    private fun getFinishView(): TextView {
        return finishMode
    }

    open fun getNoDataView(): TextView {
        return noDataTv
    }

    private fun getChooseAllLayout(): LinearLayout {
        return chooseAllLayout
    }

    private fun getChooseAllImage(): ImageView {
        return chooseAllImage
    }

    private fun getDeleteView(): TextView {
        return deleteTextView
    }

    override fun getContentView(): Int {
        return R.layout.activity_base_manager
    }

    @SuppressLint("InflateParams")
    override fun initView(savedInstanceState: Bundle?) {
        modeLayout.visibility = View.VISIBLE
        getAddView().setOnClickListener {
            startActivity(getAddIntent())
        }
        getNoDataView().setOnClickListener {
            startActivity(getAddIntent())
        }
        getEditView().setOnClickListener {
            editMode()
        }
        getFinishView().setOnClickListener {
            normalMode()
        }
        chooseAllLayoutListener()
        getDeleteView().setOnClickListener {
            val deleteData = ArrayList<T>()
            for (position in 0 until editData.size) {
                if (editData[position]) {
                    deleteData.add(data[position])
                }
            }
            toDeleteData(deleteData)
        }
        val headerView = layoutInflater.inflate(R.layout.layout_search, null)
        getRecycleView().layoutManager = LinearLayoutManager(this)
        getRecycleView().addHeaderView(headerView)
        headerView.setOnClickListener {
            val intent = toSearchIntent()
            if (intent != null) {
                startActivity(intent)
            }
        }
        setAdapter()
        getDataList()
    }

    abstract fun setAdapter()

    abstract fun modeChange()

    override fun initData() {
        databaseStore = DatabaseStore<T>(lifecycle, getDataClass())
    }

    abstract fun getDataClass(): Class<T>
    /**
     * 搜索界面
     */
    abstract fun toSearchIntent(): Intent?

    /**
     * 添加搜索界面
     */
    abstract fun getAddIntent(): Intent

    /**
     * 删除选中的数据
     */
    open fun toDeleteData(list: ArrayList<T>) {
        databaseStore.getBox().remove(list)
    }

    private fun chooseAllLayoutListener() {
        getChooseAllLayout().setOnClickListener {
            chooseAllMode = !chooseAllMode
            chooseAllChange()
            getRecycleView().adapter?.notifyDataSetChanged()
        }

    }

    private fun chooseAllChange() {
        if (data.isNotEmpty() && data.size == editData.size) {
            for (position in 0 until data.size) {
                editData[position] = chooseAllMode
            }
        }
        if (chooseAllMode) {
            getChooseAllImage().setImageDrawable(findDrawable(R.drawable.radio_on))
        } else {
            getChooseAllImage().setImageDrawable(findDrawable(R.drawable.radio_off))
        }
    }

    override fun onBackAction() {
        if (isEditMode) {
            normalMode()
        } else {
            super.onBackAction()
        }
    }

    private fun editMode() {
        this.isEditMode = true
        getAddView().visibility = View.GONE
        getEditView().visibility = View.GONE
        getFinishView().visibility = View.VISIBLE
        modeChange()
        getBottomModeLayout().visibility = View.VISIBLE
        getRecycleView().adapter?.notifyDataSetChanged()
    }

    private fun normalMode() {
        this.isEditMode = false
        this.chooseAllMode = false
        chooseAllChange()
        getAddView().visibility = View.VISIBLE
        getEditView().visibility = View.VISIBLE
        getFinishView().visibility = View.GONE
        modeChange()
        getBottomModeLayout().visibility = View.GONE
        getRecycleView().adapter?.notifyDataSetChanged()
    }

    /**
     * 获取查询条件
     */
    abstract fun getQueryBuild(): QueryBuilder<T>

    open fun getDataList() {
        databaseStore.getQueryData(getQueryBuild().build()) {
            data.clear()
            data.addAll(it)
            if (data.isEmpty()) {
                getNoDataView().visibility = View.VISIBLE
            } else {
                getNoDataView().visibility = View.GONE
            }
            editData.clear()
            for (i in 0 until data.size) {
                editData.add(chooseAllMode)
            }
            getRecycleView().adapter?.notifyDataSetChanged()
        }
    }


}