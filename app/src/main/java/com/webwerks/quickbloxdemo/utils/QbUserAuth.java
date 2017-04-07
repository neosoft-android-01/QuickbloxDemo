package com.webwerks.quickbloxdemo.utils;

import android.os.Bundle;
import android.util.Log;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.model.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.webwerks.quickbloxdemo.model.CustomQbUser;

/**
 * Created by webwerks on 7/4/17.
 */

public class QbUserAuth {

    public static String TAG="QbUserAuth";

    public static void createNewUser(final CustomQbUser user) throws QBResponseException {

        QBAuth.createSession(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {
                QBUsers.signUpSignInTask(getQbUser(user), new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Log.e(TAG,"User Created successfully");
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Log.e(TAG,"Error in user creation " + e.getMessage());
                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {
                Log.e(TAG,"Error in Session creation " + e.getMessage());
            }
        });
    }

    public static void login(QBUser user){
        QBUsers.signIn(user, new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(QBUser user, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    private static QBUser getQbUser(CustomQbUser user){
        QBUser qbUser=new QBUser();
        qbUser.setFullName(user.getFirstName() + " " + user.getLastName());
        qbUser.setEmail(user.getEmail());
        qbUser.setPassword(user.getPassword());
        return qbUser;
    }

}
