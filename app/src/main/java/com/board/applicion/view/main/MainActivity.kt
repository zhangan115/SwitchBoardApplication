package com.board.applicion.view.main

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem
import com.board.applicion.R
import com.board.applicion.app.App
import com.za.android.lab.base.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : BaseActivity(), EasyPermissions.PermissionCallbacks {

    override fun initView() {
        bottomNavigation.addItem(AHBottomNavigationItem("压板核查", R.mipmap.ic_launcher))
        bottomNavigation.addItem(AHBottomNavigationItem("查询统计", R.mipmap.ic_launcher))
        bottomNavigation.addItem(AHBottomNavigationItem("配置管理", R.mipmap.ic_launcher))
        bottomNavigation.setTitleTextSizeInSp(14f, 14f)
        bottomNavigation.setBackgroundColor(findColor(R.color.colorWhite))
        bottomNavigation.defaultBackgroundColor = findColor(R.color.colorWhite)
        bottomNavigation.isForceTint = false
        bottomNavigation.titleState = AHBottomNavigation.TitleState.ALWAYS_SHOW
        bottomNavigation.accentColor = findColor(R.color.colorAccent)
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
