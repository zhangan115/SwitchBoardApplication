package com.board.applicion.view.cable

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.board.applicion.R
import com.board.applicion.base.BaseActivity
import org.json.JSONObject
import java.lang.Exception
import java.lang.ref.WeakReference

class CableListActivity : BaseActivity() {

    private var scannerBr: ScannerBr? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerReceiver(scannerBr, IntentFilter("com.barcode.sendBroadcast"))
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(scannerBr)
        } catch (e: Exception) {

        }
    }

    override fun initView(savedInstanceState: Bundle?) {

    }

    override fun initData() {
        scannerBr = ScannerBr()
        scannerBr!!.cableListActivity = WeakReference(this)
    }

    override fun getContentView(): Int {
        return R.layout.activity_cable_list
    }

    override fun getToolBarTitle(): String? {
        return "电缆列表"
    }

    private fun scanner(result: String) {

    }

    class ScannerBr : BroadcastReceiver() {

        var cableListActivity: WeakReference<CableListActivity>? = null

        override fun onReceive(context: Context?, intent: Intent?) {
            val result = intent?.getStringExtra("BARCODE")
            if (!TextUtils.isEmpty(result)) {
                if (cableListActivity != null) {
                    cableListActivity!!.get()?.scanner(JSONObject(result).getString("CABLE_ID"))
                }
            }
        }

    }
}