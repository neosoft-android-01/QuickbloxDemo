package com.webwerks.quickbloxdemo.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.webwerks.qbcore.chat.ChatManager;
import com.webwerks.qbcore.models.User;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.auth.LoginActivity;
import com.webwerks.quickbloxdemo.dashboard.DashboardActivity;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.utils.SharedPrefUtils;

import io.reactivex.functions.Consumer;

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
        if(SharedPrefUtils.getInstance().hasQbUser()){
            restoreChatSession();
        }else{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    public void restoreChatSession(){
        if(ChatManager.getInstance().isLogged()){
            startActivity(new Intent(this, DashboardActivity.class));
            finish();
        }else{
            loginChat();
        }

    }

    public void loginChat(){
        ChatManager.getInstance().loginToChat(App.getAppInstance().getCurrentUser()).subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                finish();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                loginChat();
            }
        });
    }
}
