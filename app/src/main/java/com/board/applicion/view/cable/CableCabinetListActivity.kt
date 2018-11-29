package com.board.applicion.view.cable

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.board.applicion.R
import com.board.applicion.base.BaseActivity
import com.board.applicion.mode.SPConstant
import com.board.applicion.mode.cable.CabinetBean
import com.board.applicion.mode.cable.CableApi
import com.board.applicion.mode.cable.CableHttpManager
import com.library.utils.SPHelper
import kotlinx.android.synthetic.main.activity_cabinet_list.*

class CableCabinetListActivity : BaseActivity() {

    private val dataList = ArrayList<CabinetBean>()

    override fun initView(savedInstanceState: Bundle?) {
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.adapter = Adapter(dataList, intent.getStringExtra("subName"), intent.getStringExtra("title"), this)
    }

    override fun initData() {
        val baseUrl = SPHelper.readString(this, SPConstant.SP_NAME, SPConstant.SP_BASE_URL)
        if (TextUtils.isEmpty(baseUrl)) {
            //没有配置地址
            requestData()
        } else {
            requestData()
        }
    }

    override fun getContentView(): Int {
        return R.layout.activity_cabinet_list
    }

    override fun getToolBarTitle(): String? {
        return intent.getStringExtra("title")
    }

    private fun requestData() {
        val cableHttp = CableHttpManager<List<CabinetBean>>(lifecycle)
        val id = intent.getLongExtra("id", -1L)
        if (id == -1L) {
            finish()
        }
        cableHttp.requestData(cableHttp.retrofit.create(CableApi::class.java).getCabinetList(id), {
            dataList.clear()
            if (it != null)
                dataList.addAll(it)
            if (dataList.isEmpty()) {
                noDataTv.visibility = View.VISIBLE
            } else {
                noDataTv.visibility = View.GONE
            }
            recycleView.adapter?.notifyDataSetChanged()
        }, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }


    private class Adapter(private val dataList: ArrayList<CabinetBean>, private val subName: String, private val roomName: String, private val content: Context)
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
                    val intent = Intent(content, CableListActivity::class.java)
                    intent.putExtra("id", dataList[position].id)
                    intent.putExtra("title", dataList[position].name)
                    intent.putExtra("subName", subName)
                    intent.putExtra("roomName", roomName)
                    content.startActivity(intent)
                }
            }
        }
    }

    private class ViewHolder(itemView: View, val name: TextView)
        : RecyclerView.ViewHolder(itemView)
}