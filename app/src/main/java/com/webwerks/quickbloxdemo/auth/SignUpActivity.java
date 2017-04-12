package com.webwerks.quickbloxdemo.auth;

import android.databinding.DataBindingUtil;

import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.databinding.ActivitySignupBinding;
import com.webwerks.quickbloxdemo.model.CustomQbUser;
import com.webwerks.quickbloxdemo.ui.activities.BaseActivity;

/**
 * Created by webwerks on 7/4/17.
 */

public class SignUpActivity extends BaseActivity {

    @Override
    public int getContentLayout() {
        return R.layout.activity_signup;
    }

    @Override
    public void initializeUiComponents() {
        ActivitySignupBinding signupBinding= DataBindingUtil.setContentView(this,getContentLayout());
        signupBinding.setUser(new CustomQbUser());
        signupBinding.setViewModel(new SignUpViewModel(this,signupBinding));
    }
}
