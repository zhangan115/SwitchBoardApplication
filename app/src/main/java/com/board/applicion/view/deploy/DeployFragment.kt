package com.board.applicion.view.deploy

import android.content.Intent
import android.os.Bundle
import com.afollestad.materialdialogs.MaterialDialog
import com.board.applicion.R
import com.board.applicion.base.BaseFragment
import com.board.applicion.view.deploy.cabinet.CabinetManagerActivity
import com.board.applicion.view.deploy.cable.CableIPSettingActivity
import com.board.applicion.view.deploy.mainControlRoom.MainControlRoomManagerActivity
import com.board.applicion.view.deploy.substation.SubstationManagerActivity
import com.board.applicion.view.deploy.switchBoard.SwitchBoardManagerActivity
import com.board.applicion.view.deploy.template.TemplateSettingActivity
import com.board.applicion.view.deploy.user.UserManagerActivity
import com.videogo.openapi.EZOpenSDK
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
            startActivity(Intent(activity, SubstationManagerActivity::class.java))
        }
        mainControlRoomManager.setOnClickListener {
            startActivity(Intent(activity, MainControlRoomManagerActivity::class.java))
        }
        cabinetManager.setOnClickListener {
            startActivity(Intent(activity, CabinetManagerActivity::class.java))
        }
        templateSetting.setOnClickListener {
            startActivity(Intent(activity, TemplateSettingActivity::class.java))
        }
        switchBoardManager.setOnClickListener {
            startActivity(Intent(activity, SwitchBoardManagerActivity::class.java))
        }
        cableIPManager.setOnClickListener {
            startActivity(Intent(activity, CableIPSettingActivity::class.java))
        }
        videoManager.setOnClickListener {
            if (EZOpenSDK.isLogin()) {
                MaterialDialog.Builder(activity!!).content("当前已登录，是否要切换账号!")
                        .negativeText("取消")
                        .positiveText("确定")
                        .onPositive { dialog, _ ->
                    dialog.dismiss()
                    EZOpenSDK.logout()
                    EZOpenSDK.openLoginPage()
                }.build().show()
            } else {
                EZOpenSDK.openLoginPage()
            }
        }
    }
}