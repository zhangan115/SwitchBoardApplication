package com.board.applicion.view.deploy.cabinet

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
import com.board.applicion.mode.User
import com.board.applicion.mode.User_
import com.board.applicion.mode.databases.Cabinet
import com.board.applicion.mode.databases.Cabinet_
import com.board.applicion.mode.databases.MainControlRoom
import com.board.applicion.mode.databases.MainControlRoom_
import com.board.applicion.view.deploy.BaseSearchActivity
import io.objectbox.query.QueryBuilder
import kotlinx.android.synthetic.main.activity_search.*

class CabinetSearchActivity : BaseSearchActivity<Cabinet>() {

    override fun setSearchAdapter() {
        recycleView.adapter = Adapter(this.datas, this)
    }

    override fun getDataClass(): Class<Cabinet> {
        return Cabinet::class.java
    }

    override fun getQueryBuild(): QueryBuilder<Cabinet> {
        return databaseStore.getQueryBuilder().contains(Cabinet_.name, searchContentStr).equal(Cabinet_.status, 0)
    }

    override fun getHitStr(): String {
        return "请输入屏柜名称"
    }

    private class Adapter(private val dataList: ArrayList<Cabinet>, private val content: Context)
        : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(content).inflate(R.layout.item_cabinet_list, parent, false)
            val editStateImage = view.findViewById<ImageView>(R.id.editStateImage)
            val text1 = view.findViewById<TextView>(R.id.substationName)
            val text2 = view.findViewById<TextView>(R.id.roomName)
            val text3 = view.findViewById<TextView>(R.id.cabinetName)
            val text4 = view.findViewById<TextView>(R.id.configName)
            val editLayout = view.findViewById<LinearLayout>(R.id.editLayout)
            return ViewHolder(view, editStateImage, text1, text2, text3, text4, editLayout)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text1.text = dataList[position].substationToOne.target.name
            holder.text2.text = dataList[position].mainControlRoomToOne.target.name
            holder.text3.text = dataList[position].name
            holder.text4.text = "${dataList[position].rowNum} X ${dataList[position].colNum} "
            holder.itemView.setOnClickListener {
                    val intent = Intent(content, CabinetAddActivity::class.java)
                    intent.putExtra("ID", dataList[position].id)
                    content.startActivity(intent)
                }
        }
    }

    private class ViewHolder(itemView: View, val imageView: ImageView
                             , val text1: TextView
                             , val text2: TextView
                             , val text3: TextView
                             , val text4: TextView
                             , val editLayout: LinearLayout)
        : RecyclerView.ViewHolder(itemView)

}