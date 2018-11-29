package com.board.applicion.view.video

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.board.applicion.R
import com.board.applicion.base.BaseFragment
import com.board.applicion.utils.EZOpenUtils
import com.board.applicion.widget.EZUIPlayerView
import com.ezviz.opensdk.base.Constant
import com.library.utils.DisplayUtil
import com.videogo.exception.BaseException
import com.videogo.openapi.EZOpenSDK
import com.videogo.openapi.EZPlayer
import com.videogo.openapi.OnEZPlayerCallBack
import com.videogo.openapi.bean.EZDeviceInfo
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_video.*
import java.util.*

class VideoFragment : BaseFragment() {

    //获取摄像头列表
    private var requestDeviceList: Disposable? = null
    private val deviceList = ArrayList<EZDeviceInfo>()
    private var ezBr: EZbr? = null

    companion object {
        fun getFragment(): VideoFragment {
            val fragment = VideoFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun getContentView(): Int {
        return R.layout.fragment_video
    }

    override fun initData() {
        //没有登录需要登录萤石平台
        if (!EZOpenSDK.isLogin()) {
            EZOpenSDK.openLoginPage()
        } else {
            requestEZDevice()
        }
    }

    fun requestEZDevice() {
        //获取设备列表
        val observable = Observable.create<List<EZDeviceInfo>> {
            try {
                it.onNext(EZOpenSDK.getDeviceList(0, 200))
            } catch (e: Exception) {
                it.onError(e.fillInStackTrace())
            } finally {
                it.onComplete()
            }
        }
        requestDeviceList = observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    deviceList.clear()
                    if (it.isNotEmpty()) {
                        deviceList.addAll(it)
                    }
                    showDeviceToView()
                }, {
                    deviceList.clear()
                    showDeviceToView()
                }, {

                })
    }

    override fun initView() {

    }

    private fun showDeviceToView() {
        if (deviceList.isEmpty()) {
            showInfoTv.visibility = View.VISIBLE
            showInfoTv.text = "当前没有数据，请去萤石平台添加设备！"
        } else {
            showInfoTv.visibility = View.GONE
        }
        leftLayout.removeAllViews()
        rightLayout.removeAllViews()
        for (index in 0 until deviceList.size) {
            if (index % 2 == 0) {
                leftLayout.addView(createVideoLayout(deviceList[index], true))
            } else {
                rightLayout.addView(createVideoLayout(deviceList[index], false))
            }
        }
        //进行播放操作
    }

    private val ezUIlPayViews = ArrayList<EZUIPlayerView>()
    private val eZPlays = ArrayList<EZPlayer>()

    @SuppressLint("InflateParams")
    private fun createVideoLayout(device: EZDeviceInfo, isLeft: Boolean): View? {
        if (activity == null) {
            return null
        }
        val view = LayoutInflater.from(activity).inflate(R.layout.layout_video_item, null)
        val contentLayout = view.findViewById<LinearLayout>(R.id.contentLayout)
        val params = contentLayout.layoutParams as LinearLayout.LayoutParams
        params.topMargin = DisplayUtil.dip2px(activity, 10F)
        if (isLeft) {
            params.leftMargin = DisplayUtil.dip2px(activity, 10F)
            params.rightMargin = DisplayUtil.dip2px(activity, 5F)
        } else {
            params.leftMargin = DisplayUtil.dip2px(activity, 5F)
            params.rightMargin = DisplayUtil.dip2px(activity, 10F)
        }
        contentLayout.layoutParams = params
        view.findViewById<TextView>(R.id.deviceNameTv).text = device.deviceName
        val eZUIPlayerView = view.findViewById<EZUIPlayerView>(R.id.exUiPlayerView)
        val mEZPlayer = EZPlayer.createPlayer(device.cameraInfoList.first().deviceSerial, device.cameraInfoList.first().cameraNo)
        eZPlays.add(mEZPlayer)
        ezUIlPayViews.add(eZUIPlayerView)
        eZUIPlayerView.setSurfaceHolderCallback(object : SurfaceHolder.Callback {

            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {

            }

            override fun surfaceDestroyed(holder: SurfaceHolder?) {

            }

            override fun surfaceCreated(holder: SurfaceHolder?) {
                mEZPlayer?.setSurfaceHold(holder)
                playStart(mEZPlayer)
            }

        })
        mEZPlayer.setOnEZPlayerCallBack(object : OnEZPlayerCallBack {
            override fun onPlaySuccess() {

            }

            override fun onPlayFailed(e: BaseException) {

            }

            override fun onVideoSizeChange(i: Int, i1: Int) {

            }

            override fun onCompletion() {

            }
        })
        view.setOnClickListener {
            //判断设备是否在线
            if (device.status == 1) {
                val intent = Intent(activity, PlayActivity::class.java)
                intent.putExtra(PlayConstant.EXTRA_DEVICE_SERIAL, device.deviceSerial)
                intent.putExtra(PlayConstant.EXTRA_DEVICE_NAME, device.deviceName)
                intent.putExtra(PlayConstant.EXTRA_CAMERA_NO, device.cameraInfoList.first().cameraNo)
                startActivity(intent)
            }
        }
        return view
    }

    private fun playStart(play: EZPlayer) {
        if (!EZOpenUtils.isNetworkAvailable(activity)) {
            return
        }
        play.startRealPlay()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ezBr = EZbr()
        activity?.registerReceiver(ezBr, IntentFilter(Constant.OAUTH_SUCCESS_ACTION))
    }

    override fun onStop() {
        super.onStop()
        for (view in ezUIlPayViews) {

        }
        for (play in eZPlays) {
            play.stopRealPlay()
        }
    }

    override fun onResume() {
        super.onResume()
        for (play in eZPlays) {
            playStart(play)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        //清除掉请求
        requestDeviceList?.dispose()
        for (view in ezUIlPayViews) {

        }
        for (play in eZPlays) {
            play.release()
        }
        try {
            activity?.unregisterReceiver(ezBr)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    inner class EZbr : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            requestEZDevice()
        }

    }

}