package com.board.applicion.view.deploy.user

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.board.applicion.R
import com.board.applicion.app.App
import com.board.applicion.mode.User
import com.board.applicion.mode.User_
import com.board.applicion.view.deploy.BaseEditActivity
import io.objectbox.query.QueryBuilder

class UserManagerActivity : BaseEditActivity<User>() {

    private lateinit var adapter: Adapter

    override fun modeChange() {
        adapter.isEdit = isEditMode
    }

    override fun setAdapter() {
        adapter = Adapter(data, editData, this)
        getRecycleView().adapter = adapter
    }

    override fun getDataClass(): Class<User> {
        return User::class.java
    }

    override fun toSearchIntent(): Intent? {
        return null
    }

    override fun getAddIntent(): Intent {
        return Intent(this, UserAddActivity::class.java)
    }

     override fun toDeleteData(list: ArrayList<User>) {
        val deleteList = ArrayList<User>()
        for (user in list) {
            //不能删除管理员和自己
            if (TextUtils.equals(user.name, "admin") || TextUtils.equals(user.name, App.instance.getCurrentUser().name)) {
                continue
            }
            deleteList.add(user)
        }
        databaseStore.getBox().remove(deleteList)
    }

    override fun getQueryBuild(): QueryBuilder<User> {
        return databaseStore.getQueryBuilder().equal(User_.status, 0)
    }

    override fun getToolBarTitle(): String? {
        return "用户管理"
    }

    private class Adapter(private val dataList: ArrayList<User>, val editList: ArrayList<Boolean>, private val content: Context)
        : RecyclerView.Adapter<ViewHolder>() {

        var isEdit: Boolean = false

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
            if (isEdit) {
                holder.editLayout.visibility = View.VISIBLE
            } else {
                holder.editLayout.visibility = View.GONE
            }
            holder.textId.text = dataList[position].name
            holder.textName.text = dataList[position].realName
            holder.textPass.text = dataList[position].passWd
            holder.textPhone.text = dataList[position].cellPhoneNum
            if (editList[position]) {
                holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.radio_on))
            } else {
                holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.radio_off))
            }
            holder.itemView.setOnClickListener {
                if (isEdit) {
                    editList[position] = !editList[position]
                    if (editList[position]) {
                        holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.radio_on))
                    } else {
                        holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.radio_off))
                    }
                }
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