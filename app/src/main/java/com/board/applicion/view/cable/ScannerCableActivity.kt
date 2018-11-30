package com.board.applicion.view.cable

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.afollestad.materialdialogs.MaterialDialog
import com.board.applicion.R
import com.board.applicion.base.BaseActivity
import com.board.applicion.mode.cable.CableApi
import com.board.applicion.mode.cable.CableBean
import com.board.applicion.mode.cable.CableHttpManager
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

class ScannerCableActivity : BaseActivity() {

    private var scannerBr: CableListActivity.ScannerBr? = null
    private var supportRFID = true
    private var iso6C: ISO1800_6C? = null
    private var isReadRFID = true
    private var adapter: CableAdapter? = null
    private val dataList = ArrayList<CableBean>()
    private var loadingDialog: MaterialDialog? = null

    companion object {
        var huManager: UhfManager? = null
    }

    override fun initView(savedInstanceState: Bundle?) {
        readRFIDBtn.setOnClickListener { _ ->
            if (supportRFID && isReadRFID && dataList.isNotEmpty()) {
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
            } else {
                scanner("100003")
            }
        }
    }

    private fun scanner(result: String) {
        var searchCable: CableBean? = null
        var position = -1
        for (index in 0 until dataList.size) {
            if (TextUtils.equals(dataList[index].id.toString(), result)) {
                //查找到了数据
                searchCable = dataList[index]
                position = index
                break
            }
        }
        if (searchCable == null) {
            searchCable(result)
        } else {
            expandableListView.expandGroup(position, true)
        }
    }

    private fun searchCable(id: String) {
        if (loadingDialog == null) {
            loadingDialog = MaterialDialog.Builder(this).progressIndeterminateStyle(true).build()
        }
        loadingDialog!!.show()
        val cableHttp = CableHttpManager<CableBean>(lifecycle)
        cableHttp.requestData(cableHttp.retrofit?.create(CableApi::class.java)?.getCable(id), {
            if (it != null) {
                dataList.add(0, it)
                if (dataList.isNotEmpty()) {
                    noDataTv.visibility = View.GONE
                    adapter?.setData(dataList)
                    adapter?.notifyDataSetChanged()
                    expandableListView.expandGroup(0, true)
                } else {
                    noDataTv.text = "没有数据!"
                    noDataTv.visibility = View.VISIBLE
                }
            } else {
                Toast.makeText(this, "没有找到数据！", Toast.LENGTH_SHORT).show()
            }
            loadingDialog!!.dismiss()
        }, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            loadingDialog!!.dismiss()
        })
    }

    override fun initData() {
        scannerBr = CableListActivity.ScannerBr()
        huManager = UhfAdapter.getUhfManager(this)
        if (huManager != null) {
            val canOpen = huManager!!.open()
            if (canOpen) {
                iso6C = huManager!!.isO1800_6C
                DevBeep.init(this)
            } else {
                this.supportRFID = false
            }
        } else {
            this.supportRFID = false
        }
        adapter = CableAdapter(this, R.layout.item_cable_group, R.layout.item_cable_child)
        expandableListView.setAdapter(adapter)
    }

    override fun getContentView(): Int {
        return R.layout.activity_scanner_cable
    }

    override fun getToolBarTitle(): String? {
        return "查找电缆"
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
            e.printStackTrace()
        }
    }

    inner class ScannerBr : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            val result = intent?.getStringExtra("BARCODE")
            if (!TextUtils.isEmpty(result)) {
                scanner(JSONObject(result).getString("CABLE_ID"))
            }
        }
    }
}