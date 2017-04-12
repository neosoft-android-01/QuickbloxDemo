package com.webwerks.quickbloxdemo.auth;

import android.databinding.DataBindingUtil;

import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.databinding.ActivityLoginBinding;
import com.webwerks.quickbloxdemo.model.CustomQbUser;
import com.webwerks.quickbloxdemo.ui.activities.BaseActivity;

/**
 * Created by webwerks on 11/4/17.
 */

public class LoginActivity extends BaseActivity {
    @Override
    public int getContentLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void initializeUiComponents() {
        ActivityLoginBinding loginBinding= DataBindingUtil.setContentView(this,getContentLayout());
        loginBinding.setUser(new CustomQbUser());
        loginBinding.setViewModel(new LoginViewModel(this,loginBinding));
    }


}
