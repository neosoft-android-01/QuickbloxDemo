package com.webwerks.quickbloxdemo.auth;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.webwerks.qbcore.chat.ChatManager;
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

        User user=new User();
        user.email=signupBinding.getUser().getEmail();
        user.password=signupBinding.getUser().getPassword();
        user.fullName=signupBinding.getUser().getFirstName()+" "+signupBinding.getUser().getLastName();

        QbUserAuth.createNewUser(user).subscribe(new Consumer<User>() {
            @Override
            public void accept(User qbUser) throws Exception {

                QbUserAuth.login(qbUser).subscribe(new Consumer<User>() {
                    @Override
                    public void accept(User qbUser) throws Exception {
                        App.getAppInstance().hideLoading();
                        if (qbUser != null) {
                            App.getAppInstance().setCurrentUser(qbUser);
                            /*Toast.makeText(mContext, "Hello " + qbUser.fullName, Toast.LENGTH_SHORT).show();
                            navigateNext();*/
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

    public void navigateNext(){
        mContext.startActivity(new Intent(mContext, DashboardActivity.class));
        mContext.finish();
    }
}
