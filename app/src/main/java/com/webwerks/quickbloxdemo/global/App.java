package com.webwerks.quickbloxdemo.global;

import android.app.Application;

import com.quickblox.users.model.QBUser;
import com.webwerks.qbcore.auth.QBInitialization;
import com.webwerks.qbcore.models.QbUser;


/**
 * Created by webwerks on 5/4/17.
 */

public class App extends Application {

    private static App appInstance;
    private QbUser currentUser=null;

    public QbUser getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(QbUser currentUser) {
        this.currentUser = currentUser;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        appInstance=this;
        QBInitialization.initializationQb(this);
    }

    public static App getAppInstance(){
        if(appInstance==null)
            appInstance=new App();
        return appInstance;
    }
}
