package com.board.applicion.view.main

import android.os.Bundle
import android.support.v4.app.Fragment
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.board.applicion.R
import com.board.applicion.app.App
import com.board.applicion.base.BaseActivity
import com.board.applicion.view.cable.CableFragment
import com.board.applicion.view.deploy.DeployFragment
import com.board.applicion.view.examination.ExaminationFragment
import com.board.applicion.view.search.SearchFragment
import com.board.applicion.view.video.VideoFragment
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.*

class MainActivity : BaseActivity(), EasyPermissions.PermissionCallbacks {

    private var selectPosition = 0
    private var mFragments: ArrayList<Fragment>? = null

    override fun initView(savedInstanceState: Bundle?) {
        mFragments = getFragments()
        bottomNavigation.addItem(AHBottomNavigationItem("电缆巡检", R.drawable.examination_b))
        bottomNavigation.addItem(AHBottomNavigationItem("压板核查", R.drawable.examination_b))
        bottomNavigation.addItem(AHBottomNavigationItem("监测预警", R.drawable.examination_b))
        bottomNavigation.addItem(AHBottomNavigationItem("查询统计", R.drawable.search_b))
        bottomNavigation.addItem(AHBottomNavigationItem("配置管理", R.drawable.deploy_b))
        bottomNavigation.setTitleTextSizeInSp(14f, 14f)
        bottomNavigation.setBackgroundColor(findColor(R.color.colorWhite))
        bottomNavigation.defaultBackgroundColor = findColor(R.color.colorWhite)
        bottomNavigation.isForceTint = false
        bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
        bottomNavigation.accentColor = findColor(R.color.colorAccent)
        bottomNavigation.inactiveColor = findColor(R.color.colorText333)

        bottomNavigation.setOnTabSelectedListener(AHBottomNavigation.OnTabSelectedListener { position, wasSelected ->
            if (selectPosition != position) {
                val fm = supportFragmentManager
                val ft = fm.beginTransaction()
                ft.hide(mFragments!![selectPosition])
                if (mFragments!![position].isAdded) {
                    ft.show(mFragments!![position])
                } else {
                    ft.add(R.id.frame, mFragments!![position], "tag_$position")
                }
                selectPosition = position
                ft.commit()
                return@OnTabSelectedListener true
            }
            false
        })

        val fm = supportFragmentManager
        val transaction = fm.beginTransaction()
        if (savedInstanceState != null) {
            selectPosition = savedInstanceState.getInt("selectPosition")
            if (mFragments!![selectPosition].isAdded) {
                transaction.show(mFragments!![selectPosition])
            } else {
                transaction.add(R.id.frame, mFragments!![selectPosition], "tag_$selectPosition")
            }
        } else {
            transaction.add(R.id.frame, mFragments!![selectPosition], "tag_$selectPosition")
        }
        bottomNavigation.currentItem = selectPosition
        transaction.commit()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt("selectPosition", selectPosition)
    }

    private fun getFragments(): ArrayList<Fragment> {
        val fragments = ArrayList<Fragment>()
        var cableFragment: CableFragment? = supportFragmentManager.findFragmentByTag("tag_0") as CableFragment?
        if (cableFragment == null) {
            cableFragment = CableFragment.getFragment()
        }
        var examinationFragment: ExaminationFragment? = supportFragmentManager.findFragmentByTag("tag_1") as ExaminationFragment?
        if (examinationFragment == null) {
            examinationFragment = ExaminationFragment.getFragment()
        }
        var videoFragment: VideoFragment? = supportFragmentManager.findFragmentByTag("tag_2") as VideoFragment?
        if (videoFragment == null) {
            videoFragment = VideoFragment.getFragment()
        }
        var searchFragment: SearchFragment? = supportFragmentManager.findFragmentByTag("tag_3") as SearchFragment?
        if (searchFragment == null) {
            searchFragment = SearchFragment.getFragment()
        }
        var deployFragment: DeployFragment? = supportFragmentManager.findFragmentByTag("tag_4") as DeployFragment?
        if (deployFragment == null) {
            deployFragment = DeployFragment.getFragment()
        }
        fragments.add(cableFragment)
        fragments.add(examinationFragment)
        fragments.add(videoFragment)
        fragments.add(searchFragment)
        fragments.add(deployFragment)
        return fragments
    }

    override fun initData() {
        checkPermission()
    }

    override fun getContentView(): Int {
        return R.layout.activity_main
    }

    companion object {
        const val REQUEST_EXTERNAL = 10 //内存卡权限
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == REQUEST_EXTERNAL) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                AppSettingsDialog.Builder(this)
                        .setRationale(getString(R.string.need_save_setting))
                        .setTitle(getString(R.string.request_permissions))
                        .setPositiveButton(getString(R.string.sure))
                        .setNegativeButton(getString(R.string.cancel))
                        .setRequestCode(REQUEST_EXTERNAL)
                        .build()
                        .show()
            }
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {

    }

    @AfterPermissionGranted(REQUEST_EXTERNAL)
    fun checkPermission() {
        if (!EasyPermissions.hasPermissions(this.applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            EasyPermissions.requestPermissions(this, getString(R.string.request_permissions),
                    REQUEST_EXTERNAL, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        } else {
            App.instance.createPhotoDir()//创建保存图片目录
        }
    }
}
