package com.board.applicion.view.deploy.substation

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.board.applicion.R
import com.board.applicion.base.BaseActivity
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.Substation
import com.board.applicion.mode.Substation_
import kotlinx.android.synthetic.main.activity_choose_substation.*

class SubstationChooseActivity : BaseActivity() {

    lateinit var databaseStore: DatabaseStore<Substation>
    val data = ArrayList<Substation>()

    override fun initView(savedInstanceState: Bundle?) {
        val headerView = layoutInflater.inflate(R.layout.layout_search_all, null)
        recycleView.layoutManager = LinearLayoutManager(this)
        recycleView.addHeaderView(headerView)
        recycleView.adapter = Adapter(data,this)
        headerView.setOnClickListener {

        }
    }

    override fun initData() {
        databaseStore = DatabaseStore(lifecycle, Substation::class.java)
        val query = databaseStore.getQueryBuilder().equal(Substation_.status, 0).build()
        databaseStore.getQueryData(query) {
            this.data.clear()
            this.data.addAll(it)
            if (this.data.isEmpty()) {
                noDataTv.visibility = View.VISIBLE
            } else {
                noDataTv.visibility = View.GONE
            }
            recycleView.adapter?.notifyDataSetChanged()
        }
    }

    override fun getContentView(): Int {
        return R.layout.activity_choose_substation
    }

    private class Adapter(private val dataList: ArrayList<Substation>, private val content: Context)
        : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(content).inflate(R.layout.item_choose, parent, false)
            val name = view.findViewById<TextView>(R.id.name)
            return ViewHolder(view, name)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.name.text = dataList[position].name
            holder.itemView.setOnClickListener {
                if (content is SubstationChooseActivity){
                    val intent = Intent()
                    intent.putExtra("chooseId",dataList[position].id)
                    content.setResult(Activity.RESULT_OK,intent)
                    content.finish()
                }
            }
        }
    }

    private class ViewHolder(itemView: View, val name: TextView)
        : RecyclerView.ViewHolder(itemView)

}