package com.board.applicion.view.search

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.board.applicion.R
import com.board.applicion.base.BaseFragment
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.User
import com.board.applicion.mode.User_
import com.board.applicion.mode.databases.*
import com.board.applicion.view.examination.SubListAdapter
import com.board.applicion.view.examination.room.CabinetListActivity
import kotlinx.android.synthetic.main.fragment_search.*

class SearchFragment : BaseFragment(), View.OnClickListener {

    var subList: ArrayList<Substation> = ArrayList()
    var roomList: ArrayList<MainControlRoom> = ArrayList()
    var cabinetList: ArrayList<Cabinet> = ArrayList()
    var userList: ArrayList<User> = ArrayList()

    private var isFilter = false
    //search data
    private var substation: Substation? = null
    private var mainRoom: MainControlRoom? = null
    private var cabinet: Cabinet? = null
    private var checkUser: User? = null
    private var startTime: Long = -1
    private var endTime: Long = -1
    private var viewWidth: Int? = 0
    private lateinit var subStore: DatabaseStore<Substation>
    lateinit var adapter: SubListAdapter
    private val dataList = ArrayList<Substation>()
    private var conditionAdapter: Adapter? = null

    private var searchResultList: ArrayList<SearchResult>? = null
    private var searchCondition: SearchCondition? = null

    companion object {
        fun getFragment(): SearchFragment {
            val fragment = SearchFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onClick(v: View?) {
        searchResultList?.clear()
        when (v?.id) {
            R.id.chooseSubLayout -> {
                subList.clear()
                subList.addAll(subStore.getQueryBuilder().equal(Substation_.status, 0).build().find())
                if (subList.isEmpty()) return
                for (item in subList) {
                    searchResultList?.add(SearchResult(item.id, item.name))
                }
                if (substation != null) {
                    conditionAdapter?.chooseId = substation!!.id
                }
                searchCondition = SearchCondition("屏柜", 1, searchResultList!!)
            }
            R.id.chooseRoomLayout -> {
                if (substation == null) return
                roomList.clear()

                roomList.addAll(DatabaseStore(lifecycle, MainControlRoom::class.java).getQueryBuilder()
                        .equal(MainControlRoom_.subId, substation!!.id)
                        .equal(MainControlRoom_.status, 0)
                        .build().find())
                if (roomList.isEmpty()) return
                for (item in roomList) {
                    searchResultList?.add(SearchResult(item.id, item.name))
                }
                if (mainRoom != null) {
                    conditionAdapter?.chooseId = mainRoom!!.id
                }
                searchCondition = SearchCondition("主控室", 2, searchResultList!!)
            }
            R.id.chooseCabinetLayout -> {
                if (mainRoom == null) return
                cabinetList.clear()
                cabinetList.addAll(DatabaseStore(lifecycle, Cabinet::class.java).getQueryBuilder()
                        .equal(Cabinet_.mcrId, mainRoom!!.id)
                        .equal(Cabinet_.status, 0)
                        .build().find())
                if (cabinetList.isEmpty()) return
                for (item in cabinetList) {
                    searchResultList?.add(SearchResult(item.id, item.name))
                }
                if (cabinet != null) {
                    conditionAdapter?.chooseId = cabinet!!.id
                }
                searchCondition = SearchCondition("屏柜", 3, searchResultList!!)
            }
            R.id.chooseUserLayout -> {
                userList.clear()
                userList.addAll(DatabaseStore(lifecycle, User::class.java).getQueryBuilder()
                        .equal(User_.status, 0)
                        .build().find())
                if (userList.isEmpty()) return
                for (item in userList) {
                    searchResultList?.add(SearchResult(item.id, item.realName))
                }
                if (checkUser != null) {
                    conditionAdapter?.chooseId = checkUser!!.id
                }
                searchCondition = SearchCondition("检查人员", 4, searchResultList!!)
            }
        }
        if (this.searchCondition == null) return
        showSearchResult()
    }

    private fun showSearchResult() {
        if (searchCondition == null) return
        recycleView.adapter?.notifyDataSetChanged()
        if (searchCondition != null) {
            titleText.text = searchCondition!!.name
        }
        this.showOpenAnim()
    }

    private fun showOpenAnim() {
        searchLayout.visibility = View.GONE
        resultLayout.visibility = View.VISIBLE
    }

    private fun showCloseAnim() {
        searchLayout.visibility = View.VISIBLE
        resultLayout.visibility = View.GONE
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
        chooseStartLayout.setOnClickListener {

        }
        recycleView.layoutManager = GridLayoutManager(activity, 1)
        conditionAdapter = Adapter(searchResultList!!, activity!!)
        recycleView.adapter = conditionAdapter
        chooseEndLayout.setOnClickListener {

        }
        viewWidth = activity?.resources?.displayMetrics?.widthPixels
        searchLayout.layoutParams = LinearLayout.LayoutParams(viewWidth!!, LinearLayout.LayoutParams.WRAP_CONTENT)
        resultLayout.layoutParams = LinearLayout.LayoutParams(viewWidth!!, LinearLayout.LayoutParams.WRAP_CONTENT)
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
            substation = null
            subList.clear()
            roomList.clear()
            cabinetList.clear()
            mainRoom = null
            cabinet = null
            filterUIUpdate()
        }
        backSearchBtn.setOnClickListener {
            showCloseAnim()
        }
    }

    private fun filterUIUpdate() {
        if (substation == null) {
            subName.text = ""
        } else {
            subName.text = substation!!.name
        }
        if (mainRoom == null) {
            roomName.text = ""
        } else {
            roomName.text = mainRoom!!.name
        }
        if (cabinet == null) {
            cabinetName.text = ""
        } else {
            cabinetName.text = cabinet!!.name
        }
        if (checkUser == null) {
            userName.text = ""
        } else {
            userName.text = checkUser!!.realName
        }
    }

    private fun itemClick(result: SearchResult) {
        when (searchCondition?.type) {
            1 -> {
                for (item in subList) {
                    if (result.id == item.id) {
                        substation = item
                        break
                    }
                }
                roomList.clear()
                cabinetList.clear()
                mainRoom = null
                cabinet = null
            }
            2 -> {
                for (item in roomList) {
                    if (result.id == item.id) {
                        mainRoom = item
                        break
                    }
                }
                cabinetList.clear()
                cabinet = null
            }
            3 -> {
                for (item in cabinetList) {
                    if (result.id == item.id) {
                        cabinet = item
                        break
                    }
                }
            }
            4 -> {
                for (item in userList) {
                    if (result.id == item.id) {
                        checkUser = item
                        break
                    }
                }
            }
        }
        filterUIUpdate()
        showCloseAnim()
    }

    private inner class Adapter(private val dataList: ArrayList<SearchResult>, private val content: Context)
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
                itemClick(dataList[position])
            }
        }
    }

    private class ViewHolder(itemView: View, val imageView: ImageView, val textName: TextView)
        : RecyclerView.ViewHolder(itemView)
}