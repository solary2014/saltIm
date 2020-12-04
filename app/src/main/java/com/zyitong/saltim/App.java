package com.zyitong.saltim;

import android.app.Application;

import com.zyitong.saltim.im.IMManager;

import io.agora.rtm.RtmClient;

public class App extends Application {

    private static App appInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        appInstance = this;
        IMManager.getInstance().init(this);

    }


    public RtmClient getRtmClient(){
        return IMManager.getInstance().getRtmClient();
    }

    public static App getApplication() {
        return appInstance;
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        IMManager.getInstance().release();
    }
}
