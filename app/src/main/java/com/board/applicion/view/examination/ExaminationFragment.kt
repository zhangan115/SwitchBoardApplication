package com.board.applicion.view.examination

import android.os.Bundle
import com.board.applicion.R
import com.board.applicion.base.BaseFragment

class ExaminationFragment : BaseFragment() {

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

    override fun initData() {

    }

    override fun initView() {

    }
}