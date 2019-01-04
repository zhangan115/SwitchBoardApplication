package com.board.applicion.view.deploy.user

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
import com.board.applicion.view.deploy.BaseSearchActivity
import io.objectbox.query.QueryBuilder
import kotlinx.android.synthetic.main.activity_search.*

class UserSearchActivity : BaseSearchActivity<User>() {

    override fun setSearchAdapter() {
        recycleView.adapter = Adapter(this.datas,this)
    }

    override fun getDataClass(): Class<User> {
        return User::class.java
    }

    override fun getQueryBuild(): QueryBuilder<User> {
        return databaseStore.getQueryBuilder().contains(User_.name, searchContentStr).equal(User_.status, 0)
    }

    override fun getHitStr(): String {
        return "请输入用户名称"
    }

    private class Adapter(private val dataList: ArrayList<User>, private val content: Context)
        : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(content).inflate(R.layout.item_user_list, parent, false)
            val editStateImage = view.findViewById<ImageView>(R.id.editStateImage)
            val userID = view.findViewById<TextView>(R.id.userID)
            val userName = view.findViewById<TextView>(R.id.userName)
            val userPass = view.findViewById<TextView>(R.id.userPass)
            val userPhone = view.findViewById<TextView>(R.id.userPhone)
            val editLayout = view.findViewById<LinearLayout>(R.id.editLayout)
            return ViewHolder(view, editStateImage, userID, userName, userPass, userPhone, editLayout)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            holder.textId.text = dataList[position].name
            holder.textName.text = dataList[position].realName
            holder.textPass.text = dataList[position].passWd
            holder.textPhone.text = dataList[position].cellPhoneNum

            holder.itemView.setOnClickListener {
                val intent = Intent(content, UserAddActivity::class.java)
                intent.putExtra("ID", dataList[position].id)
                content.startActivity(intent)
            }
        }
    }

    private class ViewHolder(itemView: View, val imageView: ImageView
                             , val textId: TextView
                             , val textName: TextView
                             , val textPass: TextView
                             , val textPhone: TextView, val editLayout: LinearLayout)
        : RecyclerView.ViewHolder(itemView)

}

