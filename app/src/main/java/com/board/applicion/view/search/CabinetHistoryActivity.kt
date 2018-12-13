package com.board.applicion.view.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.board.applicion.R
import com.board.applicion.base.BaseActivity
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.databases.CabinetSbPosCkRst
import com.board.applicion.mode.databases.CabinetSbPosCkRst_
import com.library.utils.DataUtil
import io.objectbox.query.Query
import kotlinx.android.synthetic.main.activity_cabinet_history.*

class CabinetHistoryActivity : BaseActivity() {

    private var subId = -1L
    private var roomId = -1L
    private var cabinetId = -1L
    private var userName: String? = null
    private var startTime = -1L
    private var endTime = -1L
    private lateinit var query: Query<CabinetSbPosCkRst>
    private lateinit var store: DatabaseStore<CabinetSbPosCkRst>
    private var datas = ArrayList<CabinetSbPosCkRst>()

    override fun initView(savedInstanceState: Bundle?) {
        recycleView.layoutManager = GridLayoutManager(this, 1)
        val adapter = Adapter(datas, this)
        recycleView.adapter = adapter
        store.getQueryData(query) {
            datas.clear()
            if (it.isEmpty()) {
                noDataTv.visibility = View.VISIBLE
            } else {
                noDataTv.visibility = View.GONE
                datas.addAll(it)
            }
            recycleView.adapter?.notifyDataSetChanged()
        }
    }

    override fun initData() {
        subId = intent.getLongExtra("subId", -1L)
        roomId = intent.getLongExtra("roomId", -1L)
        cabinetId = intent.getLongExtra("cabinetId", -1L)
        if (intent.hasExtra("userId")) {
            userName = intent.getStringExtra("userId")
        }
        startTime = intent.getLongExtra("startTime", -1L)
        endTime = intent.getLongExtra("endTime", -1L)
        store = DatabaseStore(lifecycle, CabinetSbPosCkRst::class.java)
        val qb = store.getQueryBuilder()
        if (subId != -1L) {
            qb.equal(CabinetSbPosCkRst_.subId, subId)
        }
        if (roomId != -1L) {
            qb.equal(CabinetSbPosCkRst_.mcrId, roomId)
        }
        if (cabinetId != -1L) {
            qb.equal(CabinetSbPosCkRst_.cabinetId, cabinetId)
        }
        if (!TextUtils.isEmpty(userName)) {
            qb.equal(CabinetSbPosCkRst_.checker, userName)
        }
        if (startTime != -1L) {
            qb.greater(CabinetSbPosCkRst_.checkTime, startTime)
        }
        if (endTime != -1L) {
            qb.less(CabinetSbPosCkRst_.checkTime, startTime)
        }
        qb.equal(CabinetSbPosCkRst_.status, 0)
        query = qb.build()
    }

    override fun getContentView(): Int {
        return R.layout.activity_cabinet_history
    }

    override fun getToolBarTitle(): String? {
        return "压板核查历史"
    }

    private class Adapter(private val dataList: ArrayList<CabinetSbPosCkRst>, private val content: Context)
        : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(content).inflate(R.layout.item_check_history_list, parent, false)
            val text1 = view.findViewById<TextView>(R.id.text1)
            val text2 = view.findViewById<TextView>(R.id.text2)
            val text3 = view.findViewById<TextView>(R.id.text3)
            val text4 = view.findViewById<TextView>(R.id.text4)
            val text5 = view.findViewById<TextView>(R.id.text5)
            val text6 = view.findViewById<TextView>(R.id.text6)
            val text7 = view.findViewById<TextView>(R.id.stateTv)
            val imageView = view.findViewById<ImageView>(R.id.stateIv)
            return ViewHolder(view, text1, text2, text3, text4, text5, text6, text7, imageView)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text1.text = dataList[position].substationToOne.target!!.name
            holder.text2.text = dataList[position].mainControlRoomToOne.target!!.name

            val cab = dataList[position].cabinetToOne.target
            holder.text3.text = cab.name
            holder.text4.text = "${cab.rowNum} X ${cab.colNum}"
            holder.text5.text = dataList[position].checker
            holder.text6.text = DataUtil.timeFormat(dataList[position].checkTime, null)
            if (dataList[position].checkResult == 0) {
                holder.text7.text = "通过"
                holder.stateIv.setImageDrawable(content.resources.getDrawable(R.drawable.green_circle))
            } else {
                holder.text7.text = "不通过"
                holder.stateIv.setImageDrawable(content.resources.getDrawable(R.drawable.gray_circle))
            }
            holder.itemView.setOnClickListener {
                val intent = Intent(content, CabinetHistoryDetailActivity::class.java)
                intent.putExtra("id", dataList[position].id)
                content.startActivity(intent)
            }
        }
    }

    private class ViewHolder(itemView: View, val text1: TextView
                             , val text2: TextView, val text3: TextView
                             , val text4: TextView, val text5: TextView
                             , val text6: TextView, val text7: TextView, val stateIv: ImageView)
        : RecyclerView.ViewHolder(itemView)
}