package com.webwerks.qbcore.user;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.webwerks.qbcore.database.UserDbHelper;
import com.webwerks.qbcore.models.QbUser;

import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

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
                        try {
                            QBUser respoUser = QBUsers.signUp(user).perform();
                            respoUser.setPassword(user.getPassword());
                            QbUser dbUser = QbUser.fromQbUser(respoUser);
                            UserDbHelper.getInstance().saveUserToDb(dbUser);
                            return dbUser;
                        }catch (Exception e){
                            e.printStackTrace();
                            throw new Exception(getErrorMessage(e.getMessage()));
                        }
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
                        try{
                            QBUser respoUser = QBUsers.signIn(user).perform();
                            respoUser.setPassword(user.getPassword());
                            QbUser dbUser = QbUser.fromQbUser(respoUser);
                            UserDbHelper.getInstance().saveUserToDb(dbUser);
                            return dbUser;
                        }catch (Exception e){
                            e.printStackTrace();
                            throw new Exception(getErrorMessage(e.getMessage()));
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static String getErrorMessage(String exceptionMsg) {
        if (!TextUtils.isEmpty(exceptionMsg))
            switch (exceptionMsg) {
                case "Unauthorized":
                    return "Email or password combination is wrong";

                default:
                    return "Something went wrong...";
            }

        return "Something went wrong...";
    }

    public static Observable getUsers(){

        final ArrayList<QbUser> userList=new ArrayList<>();

        QBPagedRequestBuilder pagedRequestBuilder=new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(1);
        pagedRequestBuilder.setPerPage(50);

        QBUsers.getUsers(pagedRequestBuilder).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle){

            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

/*
        Observable.just(userList).flatMapIterable(new Function<Object, Iterable<?>>() {
            @Override
            public Iterable<?> apply(Object o) throws Exception {
                return null;
            }
        });
*/



    }
}
