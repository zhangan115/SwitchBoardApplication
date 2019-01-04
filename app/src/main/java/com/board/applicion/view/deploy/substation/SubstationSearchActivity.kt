package com.board.applicion.view.deploy.substation

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.board.applicion.R
import com.board.applicion.mode.databases.Substation
import com.board.applicion.mode.databases.Substation_
import com.board.applicion.view.deploy.BaseSearchActivity
import io.objectbox.query.QueryBuilder
import kotlinx.android.synthetic.main.activity_search.*

class SubstationSearchActivity : BaseSearchActivity<Substation>() {

    override fun setSearchAdapter() {
        recycleView.adapter = Adapter(this.datas, this)
    }

    override fun getDataClass(): Class<Substation> {
        return Substation::class.java
    }

    override fun getQueryBuild(): QueryBuilder<Substation> {
        return databaseStore.getQueryBuilder().contains(Substation_.name, searchContentStr).equal(Substation_.status, 0)
    }

    override fun getHitStr(): String {
        return "请输入变电站名称"
    }

    private class Adapter(private val dataList: ArrayList<Substation>, private val content: Context)
        : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(content).inflate(R.layout.item_substation_list, parent, false)
            val editStateImage = view.findViewById<ImageView>(R.id.editStateImage)
            val substationName = view.findViewById<TextView>(R.id.substationName)
            val substationLevel = view.findViewById<TextView>(R.id.substationLevel)
            val substationDes = view.findViewById<TextView>(R.id.substationDes)
            val editLayout = view.findViewById<LinearLayout>(R.id.editLayout)
            return ViewHolder(view, editStateImage, substationName, substationLevel, substationDes, editLayout)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.substationName.text = dataList[position].name
            holder.substationLevel.text = dataList[position].voltageRank
            holder.substationDes.text = dataList[position].desc
            holder.itemView.setOnClickListener {

                val intent = Intent(content, SubstationAddActivity::class.java)
                intent.putExtra("ID", dataList[position].id)
                content.startActivity(intent)
            }
        }
    }

    private class ViewHolder(itemView: View, val imageView: ImageView
                             , val substationName: TextView
                             , val substationLevel: TextView
                             , val substationDes: TextView, val editLayout: LinearLayout)
        : RecyclerView.ViewHolder(itemView)
}

