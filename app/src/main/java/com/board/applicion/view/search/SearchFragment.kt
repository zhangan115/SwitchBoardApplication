package com.board.applicion.view.search

import android.os.Bundle
import com.board.applicion.R
import com.board.applicion.base.BaseFragment

class SearchFragment : BaseFragment() {


    companion object {
        fun getFragment(): SearchFragment {
            val fragment = SearchFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getContentView(): Int {
       return  R.layout.fragment_search
    }

    override fun initData() {
    }

    override fun initView() {
    }
}