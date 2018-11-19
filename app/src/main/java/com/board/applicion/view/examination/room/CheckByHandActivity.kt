package com.board.applicion.view.examination.room

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.board.applicion.R
import com.board.applicion.base.BaseActivity
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.databases.Cabinet
import com.board.applicion.mode.databases.Cabinet_
import com.board.applicion.mode.databases.SbPosCjRstDetail
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.library.utils.SPHelper
import kotlinx.android.synthetic.main.activity_check_hand.*

class CheckByHandActivity : BaseActivity() {

    var cabinet: Cabinet? = null

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {
        val photo = intent.getStringExtra("photo")
        Glide.with(this).load(photo).into(photoImage)
        val cabinetId = intent.getLongExtra("cabinetId", 0)
        val cabinetStore = DatabaseStore(lifecycle, Cabinet::class.java)
        cabinet = cabinetStore.getQueryBuilder().equal(Cabinet_.id, cabinetId).build().findUnique()
        sbRecycleView.layoutManager = GridLayoutManager(this, cabinet!!.colNum)
        val dataStr = SPHelper.readString(this, "data", "check_data")
        val type = object : TypeToken<ArrayList<SbPosCjRstDetail>>() {}.type
        val checkDataList = Gson().fromJson<ArrayList<SbPosCjRstDetail>>(dataStr, type)
        sbRecycleView.adapter = CheckAdapter(checkDataList, this)
    }

    override fun getContentView(): Int {
        return R.layout.activity_check_hand
    }

    override fun getToolBarTitle(): String? {
        return "人工核查"
    }

    private class CheckAdapter(private val dataList: ArrayList<SbPosCjRstDetail>, private val content: Context)
        : RecyclerView.Adapter<CheckViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CheckViewHolder {
            val view = LayoutInflater.from(content).inflate(R.layout.item_switch_board, parent, false)
            val icon = view.findViewById<ImageView>(R.id.icon)
            return CheckViewHolder(view, icon)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: CheckViewHolder, position: Int) {
            when (dataList[position].posMatch) {
                0 -> holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate__off_success))
                1 -> holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate__on_fail))
                else -> holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate_b_off))
            }
        }
    }

    private class CheckViewHolder(itemView: View, val imageView: ImageView)
        : RecyclerView.ViewHolder(itemView)
}