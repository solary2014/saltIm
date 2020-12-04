package com.zyitong.saltim.ui.activity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import com.zyitong.saltim.R;
import com.zyitong.saltim.ui.BaseActivity;
import com.zyitong.saltim.viewmodel.SplashViewModel;

public class SplashAcitvity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initViewModel();
    }

    private void initViewModel() {
        SplashViewModel splashViewModel = ViewModelProviders.of(this).get(SplashViewModel.class);
    }
}
