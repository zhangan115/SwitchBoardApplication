package com.board.applicion.app

import android.app.Application
import android.os.Environment
import java.io.File
import java.util.*

class App : Application() {

    companion object {
        lateinit var instance: App
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
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