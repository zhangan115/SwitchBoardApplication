package com.board.applicion.view.deploy

import android.content.Intent
import android.os.Bundle
import com.board.applicion.R
import com.board.applicion.base.BaseFragment
import com.board.applicion.view.deploy.mainControlRoom.MainControlRoomManagerActivity
import com.board.applicion.view.deploy.substation.SubstationManagerActivity
import com.board.applicion.view.deploy.user.UserManagerActivity
import kotlinx.android.synthetic.main.fragment_deploy.*

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
        userManagerLayout.setOnClickListener {
            startActivity(Intent(activity, UserManagerActivity::class.java))
        }
        substationManager.setOnClickListener {
            startActivity(Intent(activity,SubstationManagerActivity::class.java))
        }
        mainControlRoomManager.setOnClickListener {
            startActivity(Intent(activity,MainControlRoomManagerActivity::class.java))
        }
    }
}