package com.board.applicion.view.video;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.board.applicion.R;
import com.board.applicion.base.BaseActivity;
import com.board.applicion.utils.EZOpenUtils;
import com.board.applicion.utils.WindowSizeChangeNotifier;
import com.board.applicion.widget.EZUIPlayerView;
import com.videogo.exception.BaseException;
import com.videogo.exception.ErrorCode;
import com.videogo.openapi.EZOpenSDK;
import com.videogo.openapi.EZPlayer;
import com.videogo.openapi.OnEZPlayerCallBack;

import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicBoolean;

public class PlayActivity extends BaseActivity implements SurfaceHolder.Callback, WindowSizeChangeNotifier.OnWindowSizeChangedListener {

    private static final String TAG = "PlayActivity";
    public final static int STATUS_INIT = 1;
    public final static int STATUS_START = 2;
    public final static int STATUS_PLAY = 3;
    public final static int STATUS_STOP = 4;
    public int mStatus = STATUS_INIT;
    private EZUIPlayerView mEZUIPlayerView;
    private EZPlayer mEZPlayer;
    private int mUiOptions = 0;
    private MyOrientationDetector mOrientationDetector;
    private AtomicBoolean isResumePlay = new AtomicBoolean(true);
    private AtomicBoolean isInitSurface = new AtomicBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setSurfaceSize();
    }

    @Override
    public int getContentView() {
        return R.layout.activity_play;
    }

    @Override
    public void initView(@Nullable Bundle savedInstanceState) {
        mEZUIPlayerView = findViewById(R.id.play_view);
        mEZUIPlayerView.setSurfaceHolderCallback(PlayActivity.this);
        ImageView mBack = findViewById(R.id.back);
        mBack.setOnClickListener(v -> onBackAction());
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        String mDeviceSerial = intent.getStringExtra(PlayConstant.EXTRA_DEVICE_SERIAL);
        int mCameraNo = intent.getIntExtra(PlayConstant.EXTRA_CAMERA_NO, -1);
        String deviceName = intent.getStringExtra(PlayConstant.EXTRA_DEVICE_NAME);
        TextView deviceNameTv = findViewById(R.id.deviceNameTv);
        deviceNameTv.setText(deviceName);
        if (TextUtils.isEmpty(mDeviceSerial) || mCameraNo == -1) {
            finish();
            return;
        }
        mEZPlayer = EZPlayer.createPlayer(mDeviceSerial, mCameraNo);
        mEZPlayer.setOnEZPlayerCallBack(new OnEZPlayerCallBack() {
            @Override
            public void onPlaySuccess() {
                if (mStatus != STATUS_STOP) {
                    handlePlaySuccess();
                }
            }

            @Override
            public void onPlayFailed(BaseException e) {
                handlePlayFail(e);
            }

            @Override
            public void onVideoSizeChange(int i, int i1) {

            }

            @Override
            public void onCompletion() {

            }
        });
        mOrientationDetector = new MyOrientationDetector(this);
        mUiOptions = getWindow().getDecorView().getSystemUiVisibility();
        new WindowSizeChangeNotifier(this, this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mEZPlayer != null) {
            mEZPlayer.setSurfaceHold(holder);
        }
        if (isInitSurface.compareAndSet(false, true) && isResumePlay.get()) {
            isResumePlay.set(false);
            startRealPlay();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isInitSurface.set(false);
    }

    @Override
    public void onBackAction() {
        if (mEZPlayer != null) {
            mEZPlayer.stopRealPlay();
            mEZPlayer.release();
        }
        super.onBackAction();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mOrientationDetector.enable();
        //界面stop时，如果在播放，那isResumePlay标志位置为true，resume时恢复播放
        if (isResumePlay.get() && isInitSurface.get()) {
            isResumePlay.set(false);
            startRealPlay();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mOrientationDetector.disable();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //界面stop时，如果在播放，那isResumePlay标志位置为true，以便resume时恢复播放
        if (mStatus != STATUS_STOP) {
            isResumePlay.set(true);
        }
        stopRealPlay();
    }

    @Override
    protected void onDestroy() {
        getWindow().getDecorView().setSystemUiVisibility(mUiOptions);
        if (mEZPlayer != null) {
            mEZPlayer.release();
        }
        super.onDestroy();
    }

    private void startRealPlayUI() {
        mEZUIPlayerView.showLoading();
    }

    private void realStartPlay(String mVerifyCode) {
        if (!TextUtils.isEmpty(mVerifyCode)) {
            mEZPlayer.setPlayVerifyCode(mVerifyCode);
        }
        mStatus = STATUS_START;
        startRealPlayUI();
        mEZPlayer.startRealPlay();
    }

    /**
     * 开始播放
     */
    private void startRealPlay() {
        if (mStatus == STATUS_START || mStatus == STATUS_PLAY) {
            return;
        }
        //检测网络
        if (!EZOpenUtils.isNetworkAvailable(this)) {
            return;
        }
        realStartPlay(null);
    }

    /**
     * 停止播放
     */
    private void stopRealPlay() {
        stopRealPlayUI();
        if (mEZPlayer != null) {
            mEZPlayer.stopRealPlay();
        }
    }

    /**
     * 停止播放UI
     */
    private void stopRealPlayUI() {
        mStatus = STATUS_STOP;
        refreshPlayStutsUI();
    }

    /**
     * 处理播放成功的情况
     */
    private void handlePlaySuccess() {
        mStatus = STATUS_PLAY;
        refreshPlayStutsUI();
    }

    /**
     * 更新播放状态显示UI
     */
    private void refreshPlayStutsUI() {
        switch (mStatus) {
            case STATUS_PLAY:
                mEZUIPlayerView.dismissomLoading();
                break;
            case STATUS_STOP:
                mEZUIPlayerView.dismissomLoading();
                break;
            default:
                break;
        }
    }

    /**
     * 处理播放失败的情况
     */
    private void handlePlayFail(BaseException e) {
        if (mStatus != STATUS_STOP) {
            mStatus = STATUS_STOP;
            mEZUIPlayerView.dismissomLoading();
            stopRealPlay();
            updateRealPlayFailUI(e.getErrorCode());
        }
    }


    private void updateRealPlayFailUI(int errorCode) {
        String txt = null;
        Log.i(TAG, "updateRealPlayFailUI: errorCode:" + errorCode);
        // 判断返回的错误码
        switch (errorCode) {
            case ErrorCode.ERROR_TRANSF_ACCESSTOKEN_ERROR:
                EZOpenSDK.openLoginPage();
                return;
            case ErrorCode.ERROR_CAS_MSG_PU_NO_RESOURCE:
                txt = getString(R.string.remoteplayback_over_link);
                break;
            case ErrorCode.ERROR_TRANSF_DEVICE_OFFLINE:
                txt = getString(R.string.realplay_fail_device_not_exist);
                break;
            case ErrorCode.ERROR_INNER_STREAM_TIMEOUT:
                txt = getString(R.string.realplay_fail_connect_device);
                break;
            case ErrorCode.ERROR_WEB_CODE_ERROR:
                break;
            case ErrorCode.ERROR_WEB_HARDWARE_SIGNATURE_OP_ERROR:
                break;
            case ErrorCode.ERROR_TRANSF_TERMINAL_BINDING:
                txt = "请在萤石客户端关闭终端绑定";
                break;
            // 收到这两个错误码，可以弹出对话框，让用户输入密码后，重新取流预览
            case ErrorCode.ERROR_INNER_VERIFYCODE_NEED:
            case ErrorCode.ERROR_INNER_VERIFYCODE_ERROR: {

            }
            break;
            case ErrorCode.ERROR_EXTRA_SQUARE_NO_SHARING:
            default:
                break;
        }

        if (!TextUtils.isEmpty(txt)) {
            Toast.makeText(this,txt,Toast.LENGTH_SHORT).show();
            setRealPlayFailUI(txt);
        }
    }

    private void setRealPlayFailUI(String txt) {
        mEZUIPlayerView.showTipText(txt);
    }

    @SuppressLint("ResourceAsColor")
    private void setOrientation(int orientation) {
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().getDecorView().setSystemUiVisibility(mUiOptions);
        } else {
            WindowManager.LayoutParams attr = getWindow().getAttributes();
            attr.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attr);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            getWindow().getDecorView().setSystemUiVisibility(mUiOptions);
        }
        setSurfaceSize();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setOrientation(newConfig.orientation);
    }

    protected void setSurfaceSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
    }

    @Override
    public void onWindowSizeChanged(int w, int h, int oldW, int oldH) {
        if (mEZUIPlayerView.getSurfaceView().getHolder() != null && h != 0) {
            setSurfaceSize();
        }
    }

    public class MyOrientationDetector extends OrientationEventListener {

        private int mLastOrientation = 0;

        MyOrientationDetector(Context context) {
            super(context);
        }

        @Override
        public void onOrientationChanged(int orientation) {
            int value = getCurrentOrientationEx(orientation);
            if (value != mLastOrientation) {
                mLastOrientation = value;
                int current = getRequestedOrientation();
                if (current == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                        || current == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                }
            }
        }

        private int getCurrentOrientationEx(int orientation) {
            int value = 0;
            if (orientation >= 315 || orientation < 45) {
                // 0度
                return value;
            }
            if (orientation < 135) {
                // 90度
                value = 90;
                return value;
            }
            if (orientation < 225) {
                // 180度
                value = 180;
                return value;
            }
            // 270度
            value = 270;
            return value;
        }
    }
}


