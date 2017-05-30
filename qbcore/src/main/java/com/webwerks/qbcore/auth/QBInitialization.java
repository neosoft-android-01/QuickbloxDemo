package com.webwerks.qbcore.auth;

import android.content.Context;


import com.quickblox.auth.session.QBSettings;
import com.webwerks.qbcore.models.QbConfig;
import com.webwerks.qbcore.utils.AssetsUtils;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by webwerks on 11/4/17.
 */

public class QBInitialization {

    public static QBInitialization initialization;
    private static final String QB_CONFIG_DEFAULT_FILE_NAME = "qb_config.json";
    private QbConfig qbConfigs;
    Context mContext;

    public static QBInitialization initializationQb(Context context){
        if(initialization==null)
            initialization=new QBInitialization(context);

        return initialization;
    }

    public QBInitialization(Context context){
        mContext=context;
        initQbConfigs();
        initCredentials();
        initRealm();
    }

    private void initQbConfigs() {
        qbConfigs = AssetsUtils.getQbConfigurationFromAssets(QB_CONFIG_DEFAULT_FILE_NAME,mContext);
    }

    public void initCredentials(){
        if (qbConfigs != null) {
            QBSettings.getInstance().init(mContext, qbConfigs.getAppId(), qbConfigs.getAuthKey(), qbConfigs.getAuthSecret());
            QBSettings.getInstance().setAccountKey(qbConfigs.getAccountKey());
        }
    }

    public void initRealm(){
        Realm.init(mContext);
        RealmConfiguration configuration=new RealmConfiguration.Builder()
                .schemaVersion(0)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(configuration);
    }

    public QbConfig getQbConfigs(){
        return qbConfigs;
    }

    protected String getQbConfigFileName(){
        return QB_CONFIG_DEFAULT_FILE_NAME;
    }

}
