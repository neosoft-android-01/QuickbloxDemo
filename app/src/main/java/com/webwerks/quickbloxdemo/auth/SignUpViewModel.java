package com.webwerks.quickbloxdemo.auth;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.webwerks.qbcore.models.User;
import com.webwerks.qbcore.user.QbUserAuth;
import com.webwerks.quickbloxdemo.dashboard.DashboardActivity;
import com.webwerks.quickbloxdemo.databinding.SignUpBinding;
import com.webwerks.quickbloxdemo.global.App;

import io.reactivex.functions.Consumer;

/**
 * Created by webwerks on 7/4/17.
 */

public class SignUpViewModel {

    private SignUpBinding signupBinding;
    Activity mContext;

    public SignUpViewModel(Activity context,SignUpBinding binding){
        signupBinding=binding;
        mContext=context;
    }

    public void onSignUpClick(){
        App.getAppInstance().showLoading(mContext);
        QbUserAuth.createNewUser(signupBinding.getUser().getQBUser()).subscribe(new Consumer<User>() {
            @Override
            public void accept(User qbUser) throws Exception {

                QbUserAuth.login(User.toQBUser(qbUser)).subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User qbUser) throws Exception {
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

                /*App.getAppInstance().hideLoading();
                if(qbUser!=null) {
                    App.getAppInstance().setCurrentUser(qbUser);
                    Toast.makeText(mContext, "User Registered successfully", Toast.LENGTH_SHORT).show();
                    navigateNext();
                }*/

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                App.getAppInstance().hideLoading();
                Toast.makeText(mContext, throwable.getMessage() +"", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void navigateNext(){
        mContext.startActivity(new Intent(mContext, DashboardActivity.class));
        mContext.finish();
    }
}
