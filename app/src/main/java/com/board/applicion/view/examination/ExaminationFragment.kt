package com.board.applicion.view.examination

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.board.applicion.R
import com.board.applicion.app.App
import com.board.applicion.base.BaseFragment
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.databases.Substation
import com.board.applicion.view.examination.room.CabinetListActivity
import kotlinx.android.synthetic.main.fragment_examination.*

class ExaminationFragment : BaseFragment() {

    lateinit var adapter: SubListAdapter
    private val dataList = ArrayList<Substation>()

    companion object {
        fun getFragment(): ExaminationFragment {
            val fragment = ExaminationFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getContentView(): Int {
        return R.layout.fragment_examination
    }

    private lateinit var subStore: DatabaseStore<Substation>
    override fun initData() {
        subStore = DatabaseStore(lifecycle, Substation::class.java)

    }

    override fun initView() {
        userText.text = "欢迎，${App.instance.getCurrentUser().realName}"
        adapter = SubListAdapter(activity, R.layout.item_sub, R.layout.item_room)
        adapter.setData(dataList)
        adapter.setItemListener {
            val intent = Intent(activity, CabinetListActivity::class.java)
            intent.putExtra("title", it.name)
            intent.putExtra("id", it.id)
            startActivity(intent)
        }
        expandableListView.setAdapter(adapter)
        subStore.getQueryData(subStore.getQueryBuilder().build()) {
            dataList.clear()
            dataList.addAll(it)
            adapter.notifyDataSetChanged()
            for (i in 0 until expandableListView.count) {
                expandableListView.expandGroup(i)
            }
        }
    }
}