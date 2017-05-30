package com.webwerks.quickbloxdemo.auth;

import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.databinding.LoginBinding;
import com.webwerks.quickbloxdemo.model.CustomQbUser;
import com.webwerks.quickbloxdemo.ui.activities.BaseActivity;

/**
 * Created by webwerks on 11/4/17.
 */

public class LoginActivity extends BaseActivity<LoginBinding> {
    @Override
    public int getContentLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void initializeUiComponents(LoginBinding binding) {
        binding.setUser(new CustomQbUser());
        binding.setViewModel(new LoginViewModel(this,binding));
    }

}
