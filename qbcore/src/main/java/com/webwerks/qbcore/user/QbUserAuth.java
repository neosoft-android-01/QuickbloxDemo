package com.webwerks.qbcore.user;

import android.os.Bundle;
import android.util.Log;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.webwerks.qbcore.models.QbUser;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Created by webwerks on 7/4/17.
 */

public class QbUserAuth {

    public static String TAG="QbUserAuth";

    public static Single<QbUser> createNewUser(final QBUser user){
        return Single.just(user)
                .map(new Function<QBUser, QbUser>() {
                    @Override
                    public QbUser apply(QBUser user) throws Exception {
                        user = QBUsers.signUp(user).perform();
                        final QbUser dbUser = QbUser.fromQbUser(user);
                        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealm(dbUser);
                            }
                        });

                        return dbUser;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Single<QbUser> login(QBUser user){
        return Single.just(user)
                .map(new Function<QBUser, QbUser>() {
                    @Override
                    public QbUser apply(QBUser user) throws Exception {
                        user = QBUsers.signIn(user).perform();
                        final QbUser dbUser = QbUser.fromQbUser(user);
                        Realm.getDefaultInstance().executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                realm.copyToRealm(dbUser);
                            }
                        });

                        return dbUser;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static void getUsers(){

        //Observable.fromArray(new ArrayList<QBUsers>).

        QBPagedRequestBuilder pagedRequestBuilder=new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(1);
        pagedRequestBuilder.setPerPage(50);

        QBUsers.getUsers(pagedRequestBuilder).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                if(qbUsers!=null && qbUsers.size()>0){
                    for(QBUser user:qbUsers){
                        Log.e(TAG,user.getFullName());
                    }
                }
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }
}
