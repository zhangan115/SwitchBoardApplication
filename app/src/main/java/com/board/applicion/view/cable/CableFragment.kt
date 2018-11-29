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
import com.board.applicion.mode.cable.CableHttpManager
import com.board.applicion.mode.cable.SubstationBean
import com.library.utils.SPHelper
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

    private val dataList = ArrayList<SubstationBean>()
    var adapter: SubListAdapter? = null

    override fun getContentView(): Int {
        return R.layout.fragment_cable
    }

    override fun initData() {
        val baseUrl = SPHelper.readString(activity, SP_NAME, SP_BASE_URL)
        adapter = SubListAdapter(activity, R.layout.item_sub, R.layout.item_room)
        adapter?.setData(dataList)
        adapter?.setItemListener { subBean, room ->
            val intent = Intent(activity, CableCabinetListActivity::class.java)
            intent.putExtra("title", room.name)
            intent.putExtra("id", room.id)
            intent.putExtra("subName",subBean.substationName)
            startActivity(intent)
        }
        expandableListView.setAdapter(adapter)
        if (TextUtils.isEmpty(baseUrl)) {
            //没有配置地址
            requestData()
        } else {
            requestData()
        }
    }

    private fun requestData() {
        val cableHttp = CableHttpManager<List<SubstationBean>>(lifecycle)
        cableHttp.requestData(cableHttp.retrofit.create(CableApi::class.java).getSubList(), {
            dataList.clear()
            if (it != null) {
                dataList.addAll(it)
            }
            adapter?.notifyDataSetChanged()
            for (i in 0 until expandableListView.count) {
                expandableListView.expandGroup(i)
            }
        }, {
            Toast.makeText(activity, it, Toast.LENGTH_SHORT).show()
        })
    }

    override fun initView() {

    }
}