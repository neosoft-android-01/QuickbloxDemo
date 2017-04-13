package com.webwerks.quickbloxdemo.ui.activities;

import android.content.Intent;

import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.auth.LoginActivity;
import com.webwerks.quickbloxdemo.chat.UserListActivity;
import com.webwerks.quickbloxdemo.utils.SharedPrefUtils;

/**
 * Created by webwerks on 13/4/17.
 */

public class SplashActivity extends BaseActivity {
    @Override
    public int getContentLayout() {
        return R.layout.activity_splash;
    }

    @Override
    public void initializeUiComponents() {
        new Thread(){
            @Override
            public void run() {
                try {
                    sleep(2000);
                    navigateNext();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void navigateNext(){
        finish();
        if(SharedPrefUtils.getInstance().hasQbUser()){
            startActivity(new Intent(this, UserListActivity.class));
        }else{
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
