package com.webwerks.qbcore.database;

import com.webwerks.qbcore.models.User;

import java.util.List;

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

    public void saveUserToDb(final User dbUser){
        try{
            realmInstance=Realm.getDefaultInstance();
            realmInstance.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {

                    // check user is present in DB then save
                    User user=realm.where(User.class).equalTo("id",dbUser.id).findFirst();
                    if(user!=null) {
                        realm.copyToRealm(dbUser);
                    }
                }
            });
        }finally {
            if(realmInstance!=null)
                realmInstance.close();
        }
    }

    public void saveUserToDb(final List<User> userList){
       for(User user:userList){
           saveUserToDb(user);
       }
    }

}
