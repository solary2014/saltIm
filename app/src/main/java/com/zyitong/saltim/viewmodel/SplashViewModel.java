package com.zyitong.saltim.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.zyitong.saltim.utils.SingleSourceLiveData;

public class SplashViewModel extends AndroidViewModel {

    private SingleSourceLiveData<Boolean> autoResult = new SingleSourceLiveData<>();
    public SplashViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<Boolean> getAutoLoginResult(){
        if (){
            autoResult.postValue(true);
        }else {
            autoResult.setSource();
        }
        return autoResult;
    }
}
