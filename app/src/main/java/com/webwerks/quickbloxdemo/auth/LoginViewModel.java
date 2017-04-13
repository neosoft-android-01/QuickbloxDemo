package com.webwerks.quickbloxdemo.auth;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.webwerks.qbcore.models.QbUser;
import com.webwerks.qbcore.user.QbUserAuth;
import com.webwerks.quickbloxdemo.chat.UserListActivity;
import com.webwerks.quickbloxdemo.databinding.ActivityLoginBinding;
import com.webwerks.quickbloxdemo.global.App;

import io.reactivex.functions.Consumer;


/**
 * Created by webwerks on 11/4/17.
 */

public class LoginViewModel {

    private ActivityLoginBinding loginBinding;
    Activity mContext;

    public LoginViewModel(Activity context,ActivityLoginBinding binding){
        loginBinding=binding;
        mContext=context;
    }

    public void onLoginClick(){
        App.getAppInstance().showLoading(mContext);
        QbUserAuth.login(loginBinding.getUser().getQBUser()).subscribe(new Consumer<QbUser>() {
            @Override
            public void accept(QbUser qbUser) throws Exception {
                App.getAppInstance().hideLoading();
                if (qbUser != null) {
                    App.getAppInstance().setCurrentUser(qbUser);
                    Toast.makeText(mContext, "Hello " + qbUser.fullName, Toast.LENGTH_SHORT).show();
                    navigateNext();
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

    public void onCreateNewUser(){
        mContext.startActivity(new Intent(mContext,SignUpActivity.class));
    }

    public void navigateNext(){
        mContext.startActivity(new Intent(mContext, UserListActivity.class));
        mContext.finish();
    }

}
