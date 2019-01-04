package com.board.applicion.view.examination.room

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.board.applicion.R
import com.board.applicion.mode.databases.Cabinet
import com.board.applicion.mode.databases.Cabinet_
import com.board.applicion.view.deploy.BaseSearchActivity
import com.board.applicion.view.search.CabinetHistoryActivity
import io.objectbox.query.QueryBuilder
import kotlinx.android.synthetic.main.activity_search.*

class CabinetSearchActivity : BaseSearchActivity<Cabinet>() {

    private var isShowHistory = false

    override fun getDataClass(): Class<Cabinet> {
        return Cabinet::class.java
    }

    override fun getQueryBuild(): QueryBuilder<Cabinet> {
        val subId = intent.getLongExtra("id", -1)
        return databaseStore.getQueryBuilder().equal(Cabinet_.mcrId, subId)
                .contains(Cabinet_.name, searchContentStr)
                .equal(Cabinet_.status, 0)
    }

    override fun getHitStr(): String {
        return "请输入屏柜名称"
    }

    override fun setSearchAdapter() {
        isShowHistory = intent.getBooleanExtra("showHistory", false)
        recycleView.adapter = Adapter(this.datas, isShowHistory, this)
    }

    private class Adapter(private val dataList: ArrayList<Cabinet>, private val isShowHistory: Boolean, private val content: Context)
        : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(content).inflate(R.layout.item_cabinet, parent, false)
            val name = view.findViewById<TextView>(R.id.name)
            return ViewHolder(view, name)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.name.text = dataList[position].name
            holder.itemView.setOnClickListener {
                if (content is Activity) {
                    if (isShowHistory) {
                        val intent = Intent(content, CabinetHistoryActivity::class.java)
                        intent.putExtra("cabinetId", dataList[position].id)
                        content.startActivity(intent)
                    } else {
                        val intent = Intent(content, SwitchBoardActivity::class.java)
                        intent.putExtra("title", dataList[position].name)
                        intent.putExtra("id", dataList[position].id)
                        content.startActivity(intent)
                    }
                }
            }
        }
    }

    private class ViewHolder(itemView: View, val name: TextView)
        : RecyclerView.ViewHolder(itemView)
}