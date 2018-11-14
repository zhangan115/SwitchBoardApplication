package com.board.applicion.app

import android.app.Application
import android.os.Environment
import com.board.applicion.mode.MyObjectBox
import com.board.applicion.mode.User
import com.google.gson.Gson
import com.library.utils.SPHelper
import java.io.File
import java.util.*
import io.objectbox.BoxStore
import org.json.JSONObject


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
        boxStore = MyObjectBox.builder().androidContext(this).build()
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