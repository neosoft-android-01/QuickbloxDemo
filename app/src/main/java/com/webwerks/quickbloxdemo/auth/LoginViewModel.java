package com.webwerks.quickbloxdemo.auth;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.webwerks.qbcore.chat.ChatManager;
import com.webwerks.qbcore.models.User;
import com.webwerks.qbcore.user.QbUserAuth;
import com.webwerks.quickbloxdemo.dashboard.DashboardActivity;
import com.webwerks.quickbloxdemo.databinding.LoginBinding;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.ui.activities.SplashActivity;

import io.reactivex.functions.Consumer;


/**
 * Created by webwerks on 11/4/17.
 */

public class LoginViewModel {

    private LoginBinding loginBinding;
    Activity mContext;

    public LoginViewModel(Activity context,LoginBinding binding){
        loginBinding=binding;
        mContext=context;
    }

    public void onLoginClick(){
        App.getAppInstance().showLoading(mContext);

        User user=new User();
        user.email=loginBinding.getUser().getEmail();
        user.password=loginBinding.getUser().getPassword();
        user.fullName=loginBinding.getUser().getFirstName()+" "+loginBinding.getUser().getEmail();

        QbUserAuth.login(user).subscribe(new Consumer<User>() {
            @Override
            public void accept(User qbUser) throws Exception {
                App.getAppInstance().hideLoading();
                if (qbUser != null) {
                    App.getAppInstance().setCurrentUser(qbUser);
                    loginChat();
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                App.getAppInstance().hideLoading();
                Toast.makeText(mContext, throwable.getMessage() +"", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loginChat(){
        ChatManager.getInstance().loginToChat(App.getAppInstance().getCurrentUser()).subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                Toast.makeText(mContext, "Hello " + user.fullName, Toast.LENGTH_SHORT).show();
                navigateNext();
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                loginChat();
            }
        });
    }

    public void onCreateNewUser(){
        mContext.startActivity(new Intent(mContext,SignUpActivity.class));
    }

    public void navigateNext(){
        mContext.startActivity(new Intent(mContext, DashboardActivity.class));
        mContext.finish();
    }
}
