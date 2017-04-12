package com.webwerks.quickbloxdemo.auth;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.core.exception.QBResponseException;
import com.webwerks.qbcore.models.QbUser;
import com.webwerks.qbcore.user.QbUserAuth;
import com.webwerks.quickbloxdemo.chat.UserListActivity;
import com.webwerks.quickbloxdemo.databinding.ActivitySignupBinding;
import com.webwerks.quickbloxdemo.global.App;

import io.reactivex.functions.Consumer;

/**
 * Created by webwerks on 7/4/17.
 */

public class SignUpViewModel {

    private ActivitySignupBinding signupBinding;
    Activity mContext;

    public SignUpViewModel(Activity context,ActivitySignupBinding binding){
        signupBinding=binding;
        mContext=context;
    }

    public void onSignUpClick(){
        QbUserAuth.createNewUser(signupBinding.getUser().getQBUser()).subscribe(new Consumer<QbUser>() {
            @Override
            public void accept(QbUser qbUser) throws Exception {
                Log.d("QB_USER",qbUser.fullName+"");
                App.getAppInstance().setCurrentUser(qbUser);
                Toast.makeText(mContext,"User Registered successfully",Toast.LENGTH_SHORT).show();
                navigateNext();
            }
        });
    }

    public void navigateNext(){
        mContext.startActivity(new Intent(mContext, UserListActivity.class));
        mContext.finish();
    }
}
