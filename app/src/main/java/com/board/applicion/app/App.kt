package com.board.applicion.app

import android.app.Application
import android.os.Environment
import android.util.Log
import com.board.applicion.BuildConfig
import com.board.applicion.mode.MyObjectBox
import com.board.applicion.mode.User
import com.google.gson.Gson
import com.library.utils.SPHelper
import com.videogo.openapi.EZOpenSDK
import java.io.File
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser




class App : Application() {

    companion object {
        lateinit var instance: App
        private var boxStore: BoxStore? = null

        fun getBoxStore(): BoxStore {
            return boxStore!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        //数据库初始化
        boxStore = MyObjectBox.builder().androidContext(this).build()
        // 萤石云开放平台 初始化
        EZOpenSDK.showSDKLog(true)
        EZOpenSDK.enableP2P(false)
        EZOpenSDK.initLib(this,"")
    }

    fun createPhotoDir() {
        val switchBoard = Environment.getExternalStorageDirectory().absolutePath + File.separator + "SwitchBoard"
        if (!File(switchBoard).exists()) {
            File(switchBoard).mkdir()
        }
    }

    fun getPhotoDir(): File? {
        val switchBoard = Environment.getExternalStorageDirectory().absolutePath + File.separator + "SwitchBoard"
        if (File(switchBoard).exists() && File(switchBoard).isDirectory) return File(switchBoard)
        return null
    }
    var user:User? = null
    fun saveCurrentUser (user:User){
        this.user = user
        SPHelper.write(this,"user","currentUser",(Gson().toJson(user)))
    }

    fun getCurrentUser() : User{
        if (user == null){
            val json = SPHelper.readString(this,"user","currentUser")
            user = Gson().fromJson(json,User::class.java)
        }
        return user!!
    }

}