package com.board.applicion.view.examination.room

import android.os.Bundle
import com.board.applicion.base.BaseActivity

class SwitchBoardActivity : BaseActivity() {

    override fun initView(savedInstanceState: Bundle?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun initData() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getContentView(): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getToolBarTitle(): String? {
        return intent.getStringExtra("title")
    }
}