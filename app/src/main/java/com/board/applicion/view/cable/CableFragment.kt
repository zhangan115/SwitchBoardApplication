package com.board.applicion.view.cable

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.board.applicion.R
import com.board.applicion.base.BaseFragment
import com.board.applicion.mode.SPConstant.SP_BASE_URL
import com.board.applicion.mode.SPConstant.SP_NAME
import com.board.applicion.mode.cable.CableApi
import com.board.applicion.mode.cable.CableBean
import com.board.applicion.mode.cable.CableHttpManager
import com.library.utils.SPHelper

class CableFragment : BaseFragment() {

    companion object {
        fun getFragment(): CableFragment {
            val fragment = CableFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    private val cableList = ArrayList<CableBean>()

    override fun getContentView(): Int {
        return R.layout.fragment_cable
    }

    override fun initData() {
        val baseUrl = SPHelper.readString(activity, SP_NAME, SP_BASE_URL)
        if (TextUtils.isEmpty(baseUrl)) {
            //没有配置地址
            requestCableList()
        } else {
            requestCableList()
        }
        startActivity(Intent(activity, CableListActivity::class.java))
    }

    /**
     * 请求电缆列表
     */
    private fun requestCableList() {
        val cableHttp = CableHttpManager<List<CableBean>>(lifecycle)
        val requestMap = HashMap<String, String>()
        cableHttp.requestData(cableHttp.retrofit.create(CableApi::class.java).getCableList(requestMap), {
            cableList.clear()
            if (it != null) {
                cableList.addAll(it)
            }

        }, {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        })
    }

    override fun initView() {

    }

    /**
     * 扫码查找电缆
     */
    private fun scanner(resultStr: String?) {

    }
}