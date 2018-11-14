package com.board.applicion.view.deploy.user

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.board.applicion.R
import com.board.applicion.base.BaseActivity
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.User
import com.board.applicion.mode.User_
import kotlinx.android.synthetic.main.activity_user_manager.*

class UserManagerActivity : BaseActivity() {

    private var isEdit = false
    private val userList = ArrayList<User>()
    private var databaseStore: DatabaseStore<User>? = null

    override fun initView(savedInstanceState: Bundle?) {
        recycleView.layoutManager = LinearLayoutManager(this)

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
            recycleView.adapter?.notifyDataSetChanged()
        }
        return true
    }

    private fun getUserList() {
        isEdit = false
        databaseStore = DatabaseStore(lifecycle, User::class.java)
        val query = databaseStore!!.getQueryBuilder().equal(User_.status, 0).build()
        databaseStore!!.getQueryData(query) {
            userList.clear()
            userList.addAll(it)
            Log.d("za","user is ${userList.size}")
            if (userList.isEmpty()) {
                noData()
            } else {
                haveData()
            }
            recycleView.adapter?.notifyDataSetChanged()
        }
    }

    private fun noData() {
        noDataTv.visibility = View.GONE
    }

    private fun haveData() {
        noDataTv.visibility = View.VISIBLE
    }
}