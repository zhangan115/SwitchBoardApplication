package com.board.applicion.view.video

import android.os.Bundle
import android.support.annotation.NonNull
import android.util.Log
import android.widget.Toast
import com.board.applicion.R
import com.board.applicion.base.BaseFragment
import com.videogo.openapi.EZOpenSDK
import com.videogo.openapi.bean.EZDeviceInfo
import io.objectbox.android.AndroidScheduler
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.internal.operators.flowable.FlowableBlockingSubscribe.subscribe
import io.reactivex.schedulers.Schedulers

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
        if (!EZOpenSDK.isLogin()) {
            EZOpenSDK.openLoginPage()
        } else {
            //获取设备列表
            val observable = Observable.create< List<EZDeviceInfo>> {
                it.onNext(EZOpenSDK.getDeviceList(0,200))
            }
            val value = observable.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {

                    }
        }

    }

    override fun initView() {

    }


}