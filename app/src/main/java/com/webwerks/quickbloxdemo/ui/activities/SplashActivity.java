package com.webwerks.quickbloxdemo.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.auth.LoginActivity;
import com.webwerks.quickbloxdemo.chat.UserListActivity;
import com.webwerks.quickbloxdemo.utils.SharedPrefUtils;

/**
 * Created by webwerks on 13/4/17.
 */

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

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
