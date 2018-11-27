package com.board.applicion.view.video

import android.os.Bundle
import com.board.applicion.R
import com.board.applicion.base.BaseFragment
import com.videogo.openapi.EZOpenSDK

class VideoFragment : BaseFragment() {

    companion object {
        fun getFragment(): VideoFragment {
            val fragment = VideoFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getContentView(): Int {
        return R.layout.fragment_video
    }

    override fun initData() {
        //没有登录需要登录萤石平台
        if (!EZOpenSDK.getInstance().isLogin){
            EZOpenSDK.getInstance().openLoginPage()
        }
    }

    override fun initView() {

    }
}