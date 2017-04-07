package com.webwerks.quickbloxdemo.auth;

import com.quickblox.core.exception.QBResponseException;
import com.webwerks.quickbloxdemo.databinding.ActivitySignupBinding;
import com.webwerks.quickbloxdemo.utils.QbUserAuth;

/**
 * Created by webwerks on 7/4/17.
 */

public class SignUpViewModel {

    private ActivitySignupBinding signupBinding;

    public SignUpViewModel(ActivitySignupBinding binding){
        signupBinding=binding;
    }

    public void onSignUpClick(){
        try {
            QbUserAuth.createNewUser(signupBinding.getUser());
        } catch (QBResponseException e) {
            e.printStackTrace();
        }
    }


}
