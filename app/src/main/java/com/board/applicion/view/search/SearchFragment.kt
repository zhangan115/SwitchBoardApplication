package com.board.applicion.view.search

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.board.applicion.R
import com.board.applicion.base.BaseFragment
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.User
import com.board.applicion.mode.databases.Cabinet
import com.board.applicion.mode.databases.MainControlRoom
import com.board.applicion.mode.databases.Substation
import com.board.applicion.view.deploy.user.UserAddActivity
import com.board.applicion.view.examination.SubListAdapter
import com.board.applicion.view.examination.room.CabinetListActivity
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment(), View.OnClickListener {

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.chooseSubLayout -> {

            }
            R.id.chooseRoomLayout -> {

            }
            R.id.chooseCabinetLayout -> {

            }
            R.id.chooseUserLayout -> {

            }
            R.id.chooseStartLayout -> {

            }
            R.id.chooseEndLayout -> {

            }

        }
    }

    var searchResultList: ArrayList<SearchResult>? = null
    var searchCondition: SearchCondition? = null

    private fun showSearchResult(searchCondition: SearchCondition) {
        this.searchCondition = searchCondition
        searchResultList?.clear()
        searchResultList?.addAll(searchCondition.resultList)
        recycleView.adapter?.notifyDataSetChanged()
    }


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
        searchResultList = ArrayList()
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
        chooseSubLayout.setOnClickListener(this)
        chooseRoomLayout.setOnClickListener(this)
        chooseCabinetLayout.setOnClickListener(this)
        chooseUserLayout.setOnClickListener(this)
        chooseStartLayout.setOnClickListener(this)
        chooseEndLayout.setOnClickListener(this)
        recycleView.adapter = Adapter(searchResultList!!, activity!!)
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
        resetBtn.setOnClickListener {
            filterUIUpdate()
        }
    }

    private fun filterUIUpdate() {

    }

    private class Adapter(private val dataList: ArrayList<SearchResult>, private val content: Context)
        : RecyclerView.Adapter<ViewHolder>() {
        var chooseId: Long = -1
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(content).inflate(R.layout.item_choose_search, parent, false)
            val chooseIv = view.findViewById<ImageView>(R.id.itemChooseIv)
            val userName = view.findViewById<TextView>(R.id.nameText)
            return ViewHolder(view, chooseIv, userName)
        }

        override fun getItemCount(): Int {
            return dataList.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.textName.text = dataList[position].name
            if (dataList[position].id == chooseId) {
                holder.imageView.visibility = View.VISIBLE
            } else {
                holder.imageView.visibility = View.INVISIBLE
            }
            holder.itemView.setOnClickListener {

            }
        }
    }

    private class ViewHolder(itemView: View, val imageView: ImageView, val textName: TextView)
        : RecyclerView.ViewHolder(itemView)
}