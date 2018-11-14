package com.board.applicion.view.deploy

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.board.applicion.base.BaseActivity
import com.board.applicion.mode.DatabaseStore
import io.objectbox.query.QueryBuilder

abstract class BaseEditActivity<T> : BaseActivity() {

    var editMode :Boolean = false
    var chooseAllMode : Boolean = false
    lateinit var databaseStore:DatabaseStore<T>
    var editData = ArrayList<Boolean>()
    val data = ArrayList<T>()

    abstract fun getRecycleView():RecyclerView

    abstract fun getBottomModeLayout():RelativeLayout

    abstract fun getAddView():TextView

    abstract fun getEditView():TextView

    abstract fun getFinishView():TextView

    abstract fun getNoDataView():TextView

    abstract fun getChooseMoreLayout():LinearLayout

    abstract fun getAddIntent():Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getAddView().setOnClickListener {
            startActivity(getAddIntent())
        }
        chooseMoreLayoutListener()
    }

    fun chooseMoreLayoutListener(){
        getChooseMoreLayout().setOnClickListener {
            chooseAllMode = !chooseAllMode
            if (data.isNotEmpty()&&data.size == editData.size){
                for (position in 0 until data.size){
                    editData[position] = chooseAllMode
                }
            }
        }
    }

    override fun onBackAction() {
        if (editMode){
            normalMode()
        }else{
            super.onBackAction()
        }
    }

    fun editMode(){
        getAddView().visibility = View.GONE
        getEditView().visibility = View.GONE
        getFinishView().visibility = View.VISIBLE

        getBottomModeLayout().visibility = View.VISIBLE
    }

    fun normalMode(){
        getAddView().visibility = View.VISIBLE
        getEditView().visibility = View.VISIBLE
        getFinishView().visibility = View.GONE

        getBottomModeLayout().visibility = View.GONE

    }

    abstract fun getQueryBuild():QueryBuilder<T>

    fun getDataList(){
      databaseStore.getQueryData(getQueryBuild().build()) {
          data.clear()
          data.addAll(it)
          if (data.isEmpty()) {
              getNoDataView().visibility = View.VISIBLE
          } else {
              getNoDataView().visibility = View.GONE
          }
          getRecycleView().adapter?.notifyDataSetChanged()
      }
    }




}