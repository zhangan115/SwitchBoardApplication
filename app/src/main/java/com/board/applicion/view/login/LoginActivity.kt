package com.board.applicion.view.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.board.applicion.R
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.User
import com.board.applicion.mode.User_
import com.board.applicion.view.main.MainActivity
import com.board.applicion.base.BaseActivity
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    override fun initView(savedInstanceState: Bundle?) {
        button.setOnClickListener { _ ->
            val nameStr = etName.text
            val passStr = etPass.text
            if (TextUtils.isEmpty(nameStr)) {
                Toast.makeText(this, "请输入账号", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(passStr)) {
                Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (TextUtils.equals("admin", nameStr.toString())) {
                if (TextUtils.equals("123456", passStr.toString())) {
                    showHome()
                } else {
                    Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show()
                }
            } else {
                val userStore = DatabaseStore(lifecycle, User::class.java)
                userStore.getQueryData(userStore.getBox().query()
                        .equal(User_.name, nameStr.toString())
                        .equal(User_.passWd, passStr.toString())
                        .build()) {
                    if (it.isEmpty()) {
                        Toast.makeText(this, "密码错误", Toast.LENGTH_SHORT).show()
                        return@getQueryData
                    }
                    showHome()
                }
            }
        }
    }

    private fun showHome() {
        startActivity(Intent(this, MainActivity::class.java))
        this.finish()
    }

    override fun initData() {

    }

    override fun getContentView(): Int {
        return R.layout.activity_login
    }

    override fun initThem() {
        setTheme(R.style.LoginActivityStyle)
        transparentStatusBar()
    }
}