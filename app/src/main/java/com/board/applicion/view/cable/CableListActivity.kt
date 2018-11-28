package com.board.applicion.view.cable

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import com.board.applicion.R
import com.board.applicion.base.BaseActivity
import com.board.applicion.utils.DevBeep
import com.olc.uhf.UhfAdapter
import com.olc.uhf.UhfManager
import com.olc.uhf.tech.ISO1800_6C
import com.olc.uhf.tech.IUhfCallback
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_cable_list.*
import org.json.JSONObject
import java.lang.ref.WeakReference

class CableListActivity : BaseActivity() {

    private var scannerBr: ScannerBr? = null
    private var supportRFID = true
    private var iso6C: ISO1800_6C? = null
    private var isReadRFID = true

    companion object {
        var huManager: UhfManager? = null
    }

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
        readRFIDBtn.setOnClickListener { _ ->
            if (supportRFID && isReadRFID) {
                isReadRFID = false
                val observable = Observable.create<String> {
                    try {
                        iso6C!!.inventory(object : IUhfCallback.Stub() {

                            override fun doInventory(list: MutableList<String>?) {
                                if (list == null) {
                                    it.onComplete()
                                    return
                                }
                                try {
                                    for (index in 0 until list.size) {
                                        if (list[index].length > 12) {
                                            val strEpc = list[index].substring(6, 12)
                                            it.onNext(strEpc)
                                        } else {
                                            it.onError(Throwable("数据错误"))
                                        }
                                    }
                                } catch (e: Exception) {
                                    it.onError(e.fillInStackTrace())
                                } finally {
                                    it.onComplete()
                                }
                            }

                            override fun doTIDAndEPC(p0: MutableList<String>?) {

                            }
                        })
                    } catch (e: Exception) {
                        it.onError(e.fillInStackTrace())
                        it.onComplete()
                    }
                }
                observable.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            scanner(it)
                            DevBeep.PlayOK()
                        }, {
                            DevBeep.PlayErr()
                            isReadRFID = true
                        }, {
                            isReadRFID = true
                        })
            }
        }
    }

    override fun initData() {
        scannerBr = ScannerBr()
        scannerBr!!.cableListActivity = WeakReference(this)
        huManager = UhfAdapter.getUhfManager(this)
        if (huManager != null) {
            val canOpen = huManager!!.open()
            if (canOpen) {
                iso6C = huManager!!.isO1800_6C
                DevBeep.init(this)
            } else {
                this.supportRFID = false
            }
        }
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