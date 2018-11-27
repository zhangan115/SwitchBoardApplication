package com.board.applicion.view.cable

import android.os.Bundle
import com.board.applicion.R
import com.board.applicion.base.BaseFragment

class CableFragment : BaseFragment() {

    companion object {
        fun getFragment(): CableFragment {
            val fragment = CableFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getContentView(): Int {
       return R.layout.fragment_cable
    }

    override fun initData() {
    }

    override fun initView() {

    }
}