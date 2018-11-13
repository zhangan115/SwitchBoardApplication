package com.board.applicion.app

import android.app.Application
import android.os.Environment
import com.board.applicion.mode.MyObjectBox
import java.io.File
import java.util.*
import io.objectbox.BoxStore


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

}