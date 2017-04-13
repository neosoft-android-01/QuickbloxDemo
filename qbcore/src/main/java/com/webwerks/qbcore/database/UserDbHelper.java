package com.webwerks.qbcore.database;

import com.webwerks.qbcore.models.QbUser;

import io.realm.Realm;

/**
 * Created by webwerks on 13/4/17.
 */

public class UserDbHelper {

    Realm realmInstance=null;
    private static UserDbHelper instance;

    public static UserDbHelper getInstance(){
        if(instance==null)
            instance=new UserDbHelper();


        return instance;
    }

   /* public UserDbHelper(){
        if(realm==null)
        realm=Realm.getDefaultInstance();
    }*/

    public void saveUserToDb(final QbUser dbUser){
        try{
            realmInstance=Realm.getDefaultInstance();
            realmInstance.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealm(dbUser);
                }
            });
        }finally {
            if(realmInstance!=null)
                realmInstance.close();
        }

    }

}
