package com.zyitong.saltim.im;

import android.content.Context;
import android.os.Looper;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.zyitong.saltim.BuildConfig;
import com.zyitong.saltim.R;
import com.zyitong.saltim.utils.ThreadManager;
import com.zyitong.saltim.utils.ToastUtils;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.agora.rtm.RtmClient;
import io.agora.rtm.RtmClientListener;
import io.agora.rtm.RtmFileMessage;
import io.agora.rtm.RtmImageMessage;
import io.agora.rtm.RtmMediaOperationProgress;
import io.agora.rtm.RtmMessage;
import io.agora.rtm.RtmStatusCode;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class IMManager {

    private static final long RECONNECTING_TIMEOUT = 5000;
    private static volatile IMManager instance;

    private RtmClient rtmClient;
    private MutableLiveData<Boolean> autologinResult = new MutableLiveData<>();
    private MutableLiveData<Boolean> kickedOffline = new MutableLiveData<>();
    private MutableLiveData<Boolean> timeoutexit = new MutableLiveData<>();
    private Disposable waitreconnect;

    public IMManager() {
    }

    public static IMManager getInstance(){
        if (instance == null){
            synchronized (IMManager.class){
                if (instance == null){
                    instance = new IMManager();
                }
            }
        }
        return instance;
    }

    public void init(Context context)  {
        initAgora(context);
    }


    private void initAgora(Context context)  {
        try {
            rtmClient = RtmClient.createInstance(context, BuildConfig.AGORA_APP_KEY, clientListener);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("NEED TO check rtm sdk init fatal error\n" + Log.getStackTraceString(e));
        }
    }



    /**
     * 自动重连结果
     *
     * @return
     */
    public LiveData<Boolean> getAutoLoginResult() {
        return autologinResult;
    }

    /**
     * 被踢监听, true 为当前为被提出状态， false 为不需要处理踢出状态
     *
     * @return
     */
    public LiveData<Boolean> getKickedOffline() {
        return kickedOffline;
    }

    /**
     * 重置被提出状态为 false
     */
    public void resetKickedOfflineState() {
        if (Looper.getMainLooper().getThread().equals(Thread.currentThread())) {
            kickedOffline.setValue(false);
        } else {
            kickedOffline.postValue(false);
        }
    }


    public RtmClient getRtmClient(){
        return rtmClient;
    }



    public void release(){
        if (waitreconnect != null && !waitreconnect.isDisposed()) {
            waitreconnect.dispose();
            waitreconnect = null;
        }
    }


    private RtmClientListener clientListener = new RtmClientListener() {
        @Override
        public void onConnectionStateChanged(int state, int reason) {
            if (state == RtmStatusCode.ConnectionState.CONNECTION_STATE_CONNECTED){
                autologinResult.postValue(true);
                if (waitreconnect != null){
                    waitreconnect.dispose();
                    waitreconnect = null;
                }
                return;
            }
            if (state == RtmStatusCode.ConnectionState.CONNECTION_STATE_DISCONNECTED){
                autologinResult.postValue(false);
                return;
            }
            if (state == RtmStatusCode.ConnectionState.CONNECTION_STATE_ABORTED){
                kickedOffline.postValue(true);
                return;
            }

            if (state == RtmStatusCode.ConnectionState.CONNECTION_STATE_RECONNECTING
                && reason == RtmStatusCode.ConnectionChangeReason.CONNECTION_CHANGE_REASON_INTERRUPTED){
                ThreadManager.getInstance().runOnUIThread(() -> ToastUtils.showToast(R.string.reconnecting));
                waitreconnect = Observable.timer(RECONNECTING_TIMEOUT, TimeUnit.MILLISECONDS).subscribe(aLong -> {
                    timeoutexit.postValue(true);
                });

            }

        }

        @Override
        public void onMessageReceived(RtmMessage rtmMessage, String s) {

        }

        @Override
        public void onImageMessageReceivedFromPeer(RtmImageMessage rtmImageMessage, String s) {

        }

        @Override
        public void onFileMessageReceivedFromPeer(RtmFileMessage rtmFileMessage, String s) {

        }

        @Override
        public void onMediaUploadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

        }

        @Override
        public void onMediaDownloadingProgress(RtmMediaOperationProgress rtmMediaOperationProgress, long l) {

        }

        @Override
        public void onTokenExpired() {

        }

        @Override
        public void onPeersOnlineStatusChanged(Map<String, Integer> map) {

        }
    };
}
