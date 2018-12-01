package com.board.applicion.view.deploy.cable

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import com.board.applicion.R
import com.board.applicion.base.BaseActivity
import com.board.applicion.mode.SPConstant
import com.library.utils.SPHelper
import kotlinx.android.synthetic.main.activity_cable_ip_setting.*

class CableIPSettingActivity : BaseActivity() {

    private var inputBaseUrl: String? = null

    override fun initView(savedInstanceState: Bundle?) {
        saveButton.setOnClickListener {
            val ipStr = ipEt.text
            if (TextUtils.isEmpty(ipStr)) {
                return@setOnClickListener
            }
            SPHelper.write(this, SPConstant.SP_NAME, SPConstant.SP_BASE_URL, ipStr.toString())
            setResult(Activity.RESULT_OK)
            finish()
        }
        inputBaseUrl = SPHelper.readString(this, SPConstant.SP_NAME, SPConstant.SP_BASE_URL)
        if (!TextUtils.isEmpty(inputBaseUrl)) {
            ipEt.setText(inputBaseUrl)
            ipEt.setSelection(inputBaseUrl!!.length)
        }
    }

    override fun initData() {

    }

    override fun getContentView(): Int {
        return R.layout.activity_cable_ip_setting
    }

    override fun getToolBarTitle(): String? {
        return "IP设置"
    }
}