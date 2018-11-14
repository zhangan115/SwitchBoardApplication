package com.board.applicion.view.deploy.user

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.board.applicion.R
import com.board.applicion.base.BaseActivity
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.User
import com.board.applicion.mode.User_
import kotlinx.android.synthetic.main.activity_user_add.*

class UserAddActivity : BaseActivity() {

    override fun initView(savedInstanceState: Bundle?) {
        saveUserButton.setOnClickListener { _ ->
            val databaseStore = DatabaseStore<User>(lifecycle, User::class.java)
            val id = editTextUserId.text.toString()
            val realName = editTextUserName.text.toString()
            val pass = editTextUserPass.text.toString()
            val phone = editTextUserPhone.text.toString()
            if (TextUtils.isEmpty(id)) {
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(realName)) {
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(pass)) {
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(phone)) {
                return@setOnClickListener
            }
            val query = databaseStore.getQueryBuilder().equal(User_.name, id).build()
            val userList = query.find()
            if (userList.isNotEmpty()){
                Toast.makeText(this, "该ID已经使用", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (databaseStore.saveData(User(0, id, realName, pass, null, phone
                            , "admin", System.currentTimeMillis(), 0))) {
                finish()
            }
        }
    }

    override fun initData() {

    }

    override fun getContentView(): Int {
        return R.layout.activity_user_add
    }

    override fun getToolBarTitle(): String? {
        return "添加人员"
    }
}