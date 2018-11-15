package com.board.applicion.view.deploy.user

import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.Toast
import com.board.applicion.R
import com.board.applicion.app.App
import com.board.applicion.base.BaseActivity
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.User
import com.board.applicion.mode.User_
import com.board.applicion.view.deploy.BaseAddActivity
import io.objectbox.query.QueryBuilder
import kotlinx.android.synthetic.main.activity_user_add.*

class UserAddActivity : BaseAddActivity<User>() {

    override fun getDataClass(): Class<User> {
        return User::class.java
    }

    override fun getQueryBuild(): QueryBuilder<User> {
        return databaseStore.getQueryBuilder().equal(User_.id, beanID)
    }

    override fun setDataToView() {
        if (bean != null) {
            editTextUserId.setText(bean!!.name)
            editTextUserName.setText(bean!!.realName)
            editTextUserPass.setText(bean!!.passWd)
            if (!TextUtils.isEmpty(bean!!.cellPhoneNum)) {
                editTextUserPhone.setText(bean!!.cellPhoneNum)
            }
        }
    }

    override fun getSaveButton(): Button {
        return saveUserButton
    }

    override fun canSave(): Boolean {
        val id = editTextUserId.text.toString()
        val realName = editTextUserName.text.toString()
        val pass = editTextUserPass.text.toString()
        val phone = editTextUserPhone.text.toString()

        if (TextUtils.isEmpty(id)) {
            return false
        }
        if (TextUtils.isEmpty(realName)) {
            return false
        }
        if (TextUtils.isEmpty(pass)) {
            return false
        }
        if (bean == null) {
            val query = databaseStore.getQueryBuilder().equal(User_.name, id).build()
            val userList = query.find()
            if (userList.isNotEmpty()) {
                Toast.makeText(this, "该ID已经使用", Toast.LENGTH_SHORT).show()
                return false
            }
            bean = User(beanID, id, realName, pass, null, phone
                    , App.instance.getCurrentUser().realName
                    , System.currentTimeMillis(), 0)
        } else {
            bean!!.name = id
            bean!!.realName = realName
            bean!!.passWd = pass
            bean!!.cellPhoneNum = phone
        }
        return true
    }

    override fun getContentView(): Int {
        return R.layout.activity_user_add
    }

    override fun getToolBarTitle(): String? {
        if (beanID == 0L) return "添加人员"
        return "修改人员"
    }
}