package com.board.applicion.view.cable

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
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
import java.lang.ref.WeakReference

class CableListActivity : BaseActivity() {

    private var scannerBr: ScannerBr? = null
    private var supportRFID = true
    private var iso6C: ISO1800_6C? = null
    private var isReadRFID = true
    private var subName: String? = null
    private var roomName: String? = null
    private var adapter: CableAdapter? = null
    private val dataList = ArrayList<CableBean>()

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
            e.printStackTrace()
        }
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
            }
        }
        noDataTv.setOnClickListener {
            requestData()
        }
    }

    override fun initData() {
        subName = intent.getStringExtra("subName")
        roomName = intent.getStringExtra("roomName")
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
        adapter = CableAdapter(this, subName, roomName, R.layout.item_cable_group, R.layout.item_cable_child)
        expandableListView.setAdapter(adapter)
        requestData()
    }

    private fun requestData() {
        noDataTv.text = "正在加载..."
        noDataTv.visibility = View.VISIBLE
        val cableHttp = CableHttpManager<List<CableBean>>(lifecycle)
        val id = intent.getLongExtra("id", -1L)
        if (id == -1L) {
            finish()
            return
        }
        cableHttp.requestData(cableHttp.retrofit.create(CableApi::class.java).getCableList(id), {
            dataList.clear()
            if (it != null) {
                dataList.addAll(it)
            }
            if (dataList.isNotEmpty()) {

                noDataTv.visibility = View.GONE
                adapter?.setData(dataList)
                adapter?.notifyDataSetChanged()
            } else {
                noDataTv.text = "没有数据,点击重试!"
                noDataTv.visibility = View.VISIBLE
            }
        }, {
            noDataTv.text = "没有数据,点击重试!."
            noDataTv.visibility = View.VISIBLE
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }


    override fun getContentView(): Int {
        return R.layout.activity_cable_list
    }

    override fun getToolBarTitle(): String? {
        return intent.getStringExtra("title")
    }

    private fun scanner(result: String) {
        var searchCable: CableBean? = null
        for (cable in dataList) {
            if (TextUtils.equals(cable.id.toString(), result)) {
                //查找到了数据
                searchCable = cable
                break
            }
        }
        if (searchCable == null) {
            Toast.makeText(this, "没有找到匹配数据", Toast.LENGTH_SHORT).show()
        }
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