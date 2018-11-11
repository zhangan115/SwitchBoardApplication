package com.board.applicion.view.splash

import android.content.Intent
import com.board.applicion.R
import com.board.applicion.view.login.LoginActivity
import com.za.android.lab.base.BaseActivity
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class SplashActivity : BaseActivity() {

    private var disposable: Disposable? = null

    override fun initView() {
    }

    override fun initData() {
        disposable = Observable.just("showLogin")
                .delay(3, TimeUnit.SECONDS)
                .subscribe {
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
    }

    override fun getContentView(): Int {
        return R.layout.activity_splash
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}