package com.board.applicion.view.deploy.user

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.board.applicion.R
import com.board.applicion.app.App
import com.board.applicion.base.BaseActivity
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.User
import com.board.applicion.mode.User_
import kotlinx.android.synthetic.main.activity_user_manager.*

class UserManagerActivity : BaseActivity() {

    private var isEdit = false
    private var isChooseAll = false
    private val userList = ArrayList<User>()
    private var databaseStore: DatabaseStore<User>? = null
    private lateinit var adapter: Adapter

    @SuppressLint("InflateParams")
    override fun initView(savedInstanceState: Bundle?) {
        recycleView.layoutManager = LinearLayoutManager(this)
        adapter = Adapter(userList, this)
        recycleView.adapter = adapter
        val headerView = layoutInflater.inflate(R.layout.layout_search, null)
        recycleView.addHeaderView(headerView)
        headerView.setOnClickListener {
            //to search
        }
        chooseAllLayout.setOnClickListener {
            isChooseAll = !isChooseAll
            for (user in userList) {
                user.toDelete = isChooseAll
            }
            if (isChooseAll) {
                chooseAllImage.setImageDrawable(findDrawable(R.drawable.radio_on))
            } else {
                chooseAllImage.setImageDrawable(findDrawable(R.drawable.radio_off))
            }
            recycleView.adapter?.notifyDataSetChanged()
        }
        deleteTextView.setOnClickListener {
            val deleteUser = ArrayList<User>()
            for (user in userList) {
                if (user.toDelete) {
                    if (TextUtils.equals(user.name, "admin") || TextUtils.equals(user.name, App.instance.getCurrentUser().name)) {
                        continue
                    }
                    deleteUser.add(user)
                }
            }
            databaseStore?.getBox()?.remove(deleteUser)
            recycleView.adapter?.notifyDataSetChanged()

        }
    }

    override fun initData() {
        getUserList()
    }

    override fun getContentView(): Int {
        return R.layout.activity_user_manager
    }

    override fun getToolBarTitle(): String? {
        return "用户管理"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.user_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == R.id.add) {
            //新增
            startActivity(Intent(this, UserAddActivity::class.java))
        } else if (item?.itemId == R.id.edit) {
            //编辑
            isEdit = !isEdit
            adapter.isEdit = isEdit
            if (isEdit) {
                deleteActionLayout.visibility = View.VISIBLE
            } else {
                deleteActionLayout.visibility = View.GONE
            }
            recycleView.adapter?.notifyDataSetChanged()
        }
        return true
    }

    override fun onBackAction() {
        if (isChooseAll) {
            isChooseAll = !isChooseAll

        } else {
            super.onBackAction()
        }
    }

    private fun getUserList() {
        isEdit = false
        databaseStore = DatabaseStore(lifecycle, User::class.java)
        val query = databaseStore!!.getQueryBuilder().equal(User_.status, 0).build()
        databaseStore!!.getQueryData(query) {
            userList.clear()
            userList.addAll(it)
            if (userList.isEmpty()) {
                noData()
            } else {
                haveData()
            }
            recycleView.adapter?.notifyDataSetChanged()
        }
    }

    private fun noData() {
        noDataTv.visibility = View.VISIBLE
    }

    private fun haveData() {
        noDataTv.visibility = View.GONE
    }

    /**
     * 编辑模式
     */
    fun editMode() {

    }

    /**
     * 正常模式
     */
    fun normalMode() {

    }

    private class Adapter(private val datas: ArrayList<User>, private val content: Context) : RecyclerView.Adapter<ViewHolder>() {

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
            return datas.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            if (isEdit) {
                holder.editLayout.visibility = View.VISIBLE
            } else {
                holder.editLayout.visibility = View.GONE
            }
            holder.textId.text = datas[position].name
            holder.textName.text = datas[position].realName
            holder.textPass.text = datas[position].passWd
            holder.textPhone.text = datas[position].cellPhoneNum
            if (datas[position].toDelete) {
                holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.radio_on))
            } else {
                holder.imageView.setImageDrawable(content.resources.getDrawable(R.drawable.radio_off))
            }
            holder.itemView.setOnClickListener {
                if (isEdit) {
                    datas[position].toDelete = !datas[position].toDelete
                    if (datas[position].toDelete) {
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