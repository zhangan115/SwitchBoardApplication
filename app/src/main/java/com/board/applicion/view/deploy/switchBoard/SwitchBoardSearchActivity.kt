package com.board.applicion.view.deploy.switchBoard

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
import com.board.applicion.mode.databases.CabinetSbPosTemplate
import com.board.applicion.mode.databases.CabinetSbPosTemplate_
import com.board.applicion.view.deploy.BaseSearchActivity
import io.objectbox.query.QueryBuilder
import kotlinx.android.synthetic.main.activity_search.*

class SwitchBoardSearchActivity : BaseSearchActivity<CabinetSbPosTemplate>() {

    override fun setSearchAdapter() {
        recycleView.adapter = Adapter(this.datas, this)
    }

    override fun getDataClass(): Class<CabinetSbPosTemplate> {
        return CabinetSbPosTemplate::class.java
    }

    override fun getQueryBuild(): QueryBuilder<CabinetSbPosTemplate> {
        return databaseStore.getQueryBuilder().contains(CabinetSbPosTemplate_.name, searchContentStr).equal(CabinetSbPosTemplate_.status, 0)
    }

    override fun getHitStr(): String {
        return "请输入压板名称"
    }

    private class Adapter(private val dataList: ArrayList<CabinetSbPosTemplate>, private val content: Context)
        : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(content).inflate(R.layout.item_switch_board_list, parent, false)
            val editStateImage = view.findViewById<ImageView>(R.id.editStateImage)
            val text1 = view.findViewById<TextView>(R.id.text1)
            val text2 = view.findViewById<TextView>(R.id.text2)
            val text3 = view.findViewById<TextView>(R.id.text3)
            val text4 = view.findViewById<TextView>(R.id.text4)
            val text5 = view.findViewById<TextView>(R.id.text5)
            val text6 = view.findViewById<TextView>(R.id.text6)
            val editLayout = view.findViewById<LinearLayout>(R.id.editLayout)
            return ViewHolder(view, editStateImage, text1, text2, text3, text4, text5, text6, editLayout)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.text1.text = dataList[position].substationToOne?.target?.name
            holder.text2.text = dataList[position].mainControlRoomToOne?.target?.name
            holder.text3.text = dataList[position].cabinetToOne?.target?.name
            val text = "${dataList[position].cabinetToOne?.target?.rowNum} X  ${dataList[position].cabinetToOne?.target?.colNum} "
            holder.text4.text = text
            holder.text5.text = dataList[position].name
            holder.text6.text = dataList[position].desc

            holder.itemView.setOnClickListener {

                val intent = Intent(content, SwitchBoardAddActivity::class.java)
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
                             , val text5: TextView
                             , val text6: TextView, val editLayout: LinearLayout)
        : RecyclerView.ViewHolder(itemView)

}

