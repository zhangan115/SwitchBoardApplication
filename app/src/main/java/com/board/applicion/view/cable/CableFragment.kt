package com.board.applicion.view.cable

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.board.applicion.R
import com.board.applicion.base.BaseFragment
import com.board.applicion.mode.SPConstant.SP_BASE_URL
import com.board.applicion.mode.SPConstant.SP_NAME
import com.board.applicion.mode.cable.CableApi
import com.board.applicion.mode.cable.CableHttpManager
import com.board.applicion.mode.cable.SubstationBean
import com.library.utils.SPHelper
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.fragment_cable.*

class CableFragment : BaseFragment() {

    companion object {
        fun getFragment(): CableFragment {
            val fragment = CableFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    private var inputBaseUrl: String? = ""
    private val dataList = ArrayList<SubstationBean>()
    var adapter: SubListAdapter? = null

    override fun getContentView(): Int {
        return R.layout.fragment_cable
    }

    override fun initData() {
        adapter = SubListAdapter(activity, R.layout.item_sub, R.layout.item_room)
        adapter?.setData(dataList)
        adapter?.setItemListener { subBean, room ->
            val intent = Intent(activity, CableCabinetListActivity::class.java)
            intent.putExtra("title", room.name)
            intent.putExtra("id", room.id)
            intent.putExtra("subName", subBean.substationName)
            startActivity(intent)
        }
        expandableListView.setAdapter(adapter)
        if (needSetBaseUrl()) {
            showSetBaseUrlDialog()
        } else {
            requestData()
        }
    }

    private fun requestData() {
        val cableHttp = CableHttpManager<List<SubstationBean>>(lifecycle)
        cableHttp.requestData(cableHttp.retrofit?.create(CableApi::class.java)?.getSubList(), {
            dataList.clear()
            if (it != null) {
                dataList.addAll(it)
            }
            if (dataList.isNotEmpty()) {
                noDataTv.visibility = View.GONE
                adapter?.notifyDataSetChanged()
                for (i in 0 until expandableListView.count) {
                    expandableListView.expandGroup(i)
                }
            } else {
                noDataTv.text = "请求失败!"
                noDataTv.visibility = View.VISIBLE
            }
        }, {
            noDataTv.text = "请检查你的IP配置!"
            noDataTv.visibility = View.VISIBLE
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        })
    }


    private fun showSetBaseUrlDialog() {
        if (TextUtils.isEmpty(inputBaseUrl)) {
            inputBaseUrl = "118.24.162.247:8080"
        }
        MaterialDialog.Builder(activity!!)
                .input("请输入请求IP", inputBaseUrl, false) { dialog, input -> inputBaseUrl = input?.toString() ?: "" }
                .negativeText("取消")
                .positiveText("确定")
                .onPositive { dialog, _ ->
                    if (dialog.inputEditText != null) {
                        inputBaseUrl = dialog.inputEditText!!.text.toString()
                    }
                    if (checkBaseUrl()) {
                        SPHelper.write(activity, SP_NAME, SP_BASE_URL, inputBaseUrl)
                        requestData()
                    }
                    dialog.dismiss()
                }.build().show()
    }

    override fun initView() {
        settingIv.setOnClickListener {
            showSetBaseUrlDialog()
        }
        scannerIv.setOnClickListener {
            startActivity(Intent(activity, ScannerCableActivity::class.java))
        }
    }

    private fun needSetBaseUrl(): Boolean {
        inputBaseUrl = SPHelper.readString(activity, SP_NAME, SP_BASE_URL)
        return TextUtils.isEmpty(inputBaseUrl)
    }

    private fun checkBaseUrl(): Boolean {
        if (TextUtils.isEmpty(inputBaseUrl)) return false
        if (!inputBaseUrl!!.startsWith("http://")) {
            inputBaseUrl = "http://$inputBaseUrl"
        }
        if (!inputBaseUrl!!.endsWith("/")) {
            inputBaseUrl = "$inputBaseUrl/"
        }
        if (inputBaseUrl!!.startsWith("http://") && inputBaseUrl!!.endsWith("/")) {
            return true
        }
        return false
    }
}