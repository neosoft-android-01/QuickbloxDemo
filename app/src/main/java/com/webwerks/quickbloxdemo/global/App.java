package com.webwerks.quickbloxdemo.global;

import android.app.Application;

import com.quickblox.core.QBSettings;
import com.webwerks.quickbloxdemo.model.QbConfig;
import com.webwerks.quickbloxdemo.utils.AssetsUtils;

/**
 * Created by webwerks on 5/4/17.
 */

public class App extends Application {

    private static App appInstance;
    private static final String QB_CONFIG_DEFAULT_FILE_NAME = "qb_config.json";
    private QbConfig qbConfigs;


    @Override
    public void onCreate() {
        super.onCreate();
        appInstance=this;
        initQbConfigs();
        initCredentials();
    }

    public static App getAppInstance(){
        if(appInstance==null)
            appInstance=new App();
        return appInstance;
    }

    private void initQbConfigs() {
        qbConfigs = AssetsUtils.getQbConfigurationFromAssets(QB_CONFIG_DEFAULT_FILE_NAME,this);
    }

    public void initCredentials(){
        if (qbConfigs != null) {
            QBSettings.getInstance().init(getApplicationContext(), qbConfigs.getAppId(), qbConfigs.getAuthKey(), qbConfigs.getAuthSecret());
            QBSettings.getInstance().setAccountKey(qbConfigs.getAccountKey());
        }
    }

    public QbConfig getQbConfigs(){
        return qbConfigs;
    }

    protected String getQbConfigFileName(){
        return QB_CONFIG_DEFAULT_FILE_NAME;
    }
}
