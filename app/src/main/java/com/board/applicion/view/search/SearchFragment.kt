package com.board.applicion.view.search

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.board.applicion.R
import com.board.applicion.base.BaseFragment
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.User
import com.board.applicion.mode.databases.Cabinet
import com.board.applicion.mode.databases.MainControlRoom
import com.board.applicion.mode.databases.Substation
import com.board.applicion.view.examination.SubListAdapter
import com.board.applicion.view.examination.room.CabinetListActivity
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment() {

    private var isFilter = false
    //search data
    private var substation: Substation? = null
    private var mainRoom: MainControlRoom? = null
    private var cabinet: Cabinet? = null
    private var checkUser: User? = null
    private var startTime: Long = -1
    private var endTime: Long = -1

    private lateinit var subStore: DatabaseStore<Substation>
    lateinit var adapter: SubListAdapter
    private val dataList = ArrayList<Substation>()

    companion object {
        fun getFragment(): SearchFragment {
            val fragment = SearchFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getContentView(): Int {
        return R.layout.fragment_search
    }

    override fun initData() {
        subStore = DatabaseStore(lifecycle, Substation::class.java)
    }

    override fun initView() {
        filerTextView.setOnClickListener {
            isFilter = !isFilter
            if (isFilter) {
                filterLayout.visibility = View.VISIBLE

            } else {
                filterLayout.visibility = View.GONE

            }
        }
        adapter = SubListAdapter(activity, R.layout.item_sub, R.layout.item_room)
        adapter.setData(dataList)
        adapter.setItemListener {
            val intent = Intent(activity, CabinetListActivity::class.java)
            intent.putExtra("title", it.name)
            intent.putExtra("id", it.id)
            intent.putExtra("showHistory", true)
            startActivity(intent)
        }
        expandableListView.setAdapter(adapter)
        subStore.getQueryData(subStore.getQueryBuilder().build()) {
            dataList.clear()
            dataList.addAll(it)
            adapter.notifyDataSetChanged()
            if (it.isEmpty()) {
                noDataTv.visibility = View.VISIBLE
            } else {
                noDataTv.visibility = View.GONE
                for (i in 0 until expandableListView.count) {
                    expandableListView.expandGroup(i)
                }
            }
        }
        resetLayout.setOnClickListener {

            filterUIUpdate()
        }
    }

    private fun filterUIUpdate() {

    }
}