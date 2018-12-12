package com.board.applicion.view.search

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.AnimationUtils
import android.widget.FrameLayout
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
        }

    }

    private var searchResultList: ArrayList<SearchResult>? = null
    private var searchCondition: SearchCondition? = null
    private val animTime = 1500L

    private fun showSearchResult(searchCondition: SearchCondition) {
        this.searchCondition = searchCondition
        searchResultList?.clear()
        searchResultList?.addAll(searchCondition.resultList)
        recycleView.adapter?.notifyDataSetChanged()
        this.showOpenAnim()
    }

    private fun showOpenAnim() {
        val tranAnim = ObjectAnimator.ofFloat(searchLayout,"translationX",0F,-viewWidth!!.toFloat())
        val resultAnim = ObjectAnimator.ofFloat(resultLayout,"translationX",viewWidth!!.toFloat(),0F)
        searchLayout.visibility = View.GONE
        resultLayout.visibility = View.VISIBLE
    }

    private fun showCloseAnim() {
        val closeAnim = AnimationUtils.loadAnimation(activity, R.anim.close_search)
        closeAnim.duration = animTime
        closeAnim.fillAfter = true
        closeAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {

            }

            override fun onAnimationStart(animation: Animation?) {
                searchLayout.visibility = View.VISIBLE
            }

        })
        searchLayout.startAnimation(closeAnim)
        val closeResultAnim = AnimationUtils.loadAnimation(activity, R.anim.close_result)
        closeResultAnim.duration = animTime
        closeResultAnim.fillAfter = true
        closeResultAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                resultLayout.visibility = View.GONE
            }

            override fun onAnimationStart(animation: Animation?) {
            }
        })
        resultLayout.startAnimation(closeResultAnim)
    }

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
        chooseStartLayout.setOnClickListener {

        }
        chooseEndLayout.setOnClickListener {

        }
        viewWidth = activity?.resources?.displayMetrics?.widthPixels
//        filterLayout.layoutParams = FrameLayout.LayoutParams(viewWidth!!*2,LinearLayout.LayoutParams.WRAP_CONTENT)
        searchLayout.layoutParams = LinearLayout.LayoutParams(viewWidth!!,LinearLayout.LayoutParams.WRAP_CONTENT)
        resultLayout.layoutParams = LinearLayout.LayoutParams(viewWidth!!, LinearLayout.LayoutParams.WRAP_CONTENT)
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
        backSearchBtn.setOnClickListener {
            showCloseAnim()
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