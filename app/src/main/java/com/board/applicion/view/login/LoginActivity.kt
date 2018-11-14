package com.board.applicion.view.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.board.applicion.R
import com.board.applicion.app.App
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.User
import com.board.applicion.mode.User_
import com.board.applicion.view.main.MainActivity
import com.board.applicion.base.BaseActivity
import com.library.utils.SPHelper
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    private lateinit var userStore : DatabaseStore<User>

    override fun initView(savedInstanceState: Bundle?) {
        button.setOnClickListener { _ ->
            val nameStr = etName.text.toString()
            val passStr = etPass.text.toString()
            if (TextUtils.isEmpty(nameStr)) {
                Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(passStr)) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (setCurrentUser(nameStr,passStr)){
                showHome()
            }
        }
    }

    private fun showHome() {
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }

    private fun setCurrentUser(nameStr:String,passStr:String):Boolean{
       val user =  userStore.getQueryBuilder().equal(User_.name, nameStr).build().findUnique()
        when {
            user!=null -> return if (TextUtils.equals(user.passWd,passStr)){
                App.instance.saveCurrentUser(user)
                true
            }else{
                Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show()
                false
            }
            TextUtils.equals("admin",nameStr) -> return if (TextUtils.equals("123456",passStr)){
                val adminUser = User(0,"admin"
                        ,"管理员"
                        ,"123456"
                        ,"",""
                        ,"admin",System.currentTimeMillis(),0)
                userStore.saveData(adminUser)
                App.instance.saveCurrentUser(adminUser)
                true
            }else{
                Toast.makeText(this, "管理员密码错误", Toast.LENGTH_SHORT).show()
                false
            }
            else -> {
                Toast.makeText(this, "用户不存在", Toast.LENGTH_SHORT).show()
                return false
            }
        }
    }

    override fun initData() {
        userStore = DatabaseStore(lifecycle, User::class.java)
    }

    override fun getContentView(): Int {
        return R.layout.activity_login
    }

    override fun initThem() {
        setTheme(R.style.LoginActivityStyle)
        transparentStatusBar()
    }
}