package com.webwerks.quickbloxdemo.auth;

import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.databinding.SignUpBinding;
import com.webwerks.quickbloxdemo.model.CustomQbUser;
import com.webwerks.quickbloxdemo.ui.activities.BaseActivity;

/**
 * Created by webwerks on 7/4/17.
 */

public class SignUpActivity extends BaseActivity<SignUpBinding> {

    @Override
    public int getContentLayout() {
        return R.layout.activity_signup;
    }

    @Override
    public void initializeUiComponents(SignUpBinding binding) {
        binding.setUser(new CustomQbUser());
        binding.setViewModel(new SignUpViewModel(this,binding));
    }


}
