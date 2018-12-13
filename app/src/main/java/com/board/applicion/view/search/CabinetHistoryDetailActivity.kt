package com.board.applicion.view.search

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
import com.board.applicion.mode.databases.CabinetSbPosCkRst
import com.board.applicion.mode.databases.CabinetSbPosCkRst_
import com.board.applicion.mode.databases.SbPosCjRstDetail
import com.library.utils.DataUtil
import kotlinx.android.synthetic.main.activity_history_detail.*

class CabinetHistoryDetailActivity : BaseActivity() {

    var cabinetCheckId = 0L
    private lateinit var store: DatabaseStore<CabinetSbPosCkRst>
    private var data = ArrayList<SbPosCjRstDetail>()

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {
        cabinetCheckId = intent.getLongExtra("id", -1)
        if (cabinetCheckId == -1L) {
            finish()
        }
        store = DatabaseStore(lifecycle, CabinetSbPosCkRst::class.java)
        val currentData = store.getQueryBuilder().equal(CabinetSbPosCkRst_.id, cabinetCheckId).build().findFirst()
        if (currentData != null) {
            text1.text = currentData.substationToOne.target!!.name
            text2.text = currentData.mainControlRoomToOne.target!!.name

            val cab = currentData.cabinetToOne.target
            text3.text = cab.name
            text4.text = "${cab.rowNum} X ${cab.colNum}"
            text5.text = currentData.checker
            text6.text = DataUtil.timeFormat(currentData.checkTime, null)
            if (currentData.checkResult == 0) {
                stateTv.text = "通过"
                stateIv.setImageDrawable(findDrawable(R.drawable.green_circle))
            } else {
                stateTv.text = "不通过"
                stateIv.setImageDrawable(findDrawable(R.drawable.gray_circle))
            }
            if (currentData.sbPosCjRstDetailToMany != null) {
                data.clear()
                val list = currentData.sbPosCjRstDetailToMany
                for (i in 1..currentData.cabinetToOne.target.rowNum) {
                    for (j in 1..currentData.cabinetToOne.target.colNum) {
                        val sb = getCurrentData(i, j, list)
                        if (sb != null)
                            data.add(sb)
                    }
                }
                sbRecycleView.layoutManager = GridLayoutManager(this, currentData.cabinetToOne.target.colNum)
                sbRecycleView.adapter = Adapter(data, this)
            }
        }
    }

    /**
     * 获取当前位置的数据
     */
    private fun getCurrentData(row: Int, col: Int, list: List<SbPosCjRstDetail>): SbPosCjRstDetail? {
        var cab: SbPosCjRstDetail? = null
        for (cab1 in list) {
            if (cab1.row == row && cab1.col == col) {
                cab = cab1
                break
            }
        }
        return cab
    }

    override fun getContentView(): Int {
        return R.layout.activity_history_detail
    }

    override fun getToolBarTitle(): String? {
        return "核查详情"
    }

    private inner class Adapter(private val dataList: ArrayList<SbPosCjRstDetail>, private val content: Context)
        : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(content).inflate(R.layout.item_switch_board, parent, false)
            val icon = view.findViewById<ImageView>(R.id.icon)
            return ViewHolder(view, icon)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (dataList[position].posMatch == 0) {
                holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate_b_on))
            } else {
                holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.press_plate_b_off))
            }
        }
    }

    private class ViewHolder(itemView: View, val imageView: ImageView)
        : RecyclerView.ViewHolder(itemView)

}