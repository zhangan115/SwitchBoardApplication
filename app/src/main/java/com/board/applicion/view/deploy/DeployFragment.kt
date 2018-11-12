package com.board.applicion.view.deploy

import android.os.Bundle
import com.board.applicion.R
import com.board.applicion.base.BaseFragment

class DeployFragment : BaseFragment() {

    companion object {
        fun getFragment(): DeployFragment {
            val fragment = DeployFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getContentView(): Int {
        return R.layout.fragment_deploy
    }

    override fun initData() {

    }

    override fun initView() {

    }
}