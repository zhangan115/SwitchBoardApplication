package com.board.applicion.view.examination

import android.os.Bundle
import android.util.Log
import com.board.applicion.R
import com.board.applicion.app.App
import com.board.applicion.base.BaseFragment
import com.board.applicion.mode.DatabaseStore
import com.board.applicion.mode.databases.Substation
import kotlinx.android.synthetic.main.fragment_examination.*

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
    private lateinit var subStore : DatabaseStore<Substation>
    override fun initData() {
        subStore = DatabaseStore(lifecycle,Substation::class.java)
        subStore.getQueryData(subStore.getQueryBuilder().build()){

        }
    }

    override fun initView() {
        userText.text = "欢迎，${App.instance.getCurrentUser().realName}"
    }
}