package com.webwerks.qbcore.user;

import android.text.TextUtils;

import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.webwerks.qbcore.database.UserDbHelper;
import com.webwerks.qbcore.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

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

    public static Single<User> createNewUser(final QBUser user){
        return Single.just(user)
                .map(new Function<QBUser, User>() {
                    @Override
                    public User apply(QBUser user) throws Exception {
                        try {
                            QBUser respoUser = QBUsers.signUp(user).perform();
                            respoUser.setPassword(user.getPassword());
                            User dbUser = User.fromQbUser(respoUser);
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

    public static Single<User> login(QBUser user){
        return Single.just(user)
                .map(new Function<QBUser, User>() {
                    @Override
                    public User apply(QBUser user) throws Exception {
                        try{
                            QBUser respoUser = QBUsers.signIn(user).perform();
                            respoUser.setPassword(user.getPassword());
                            User dbUser = User.fromQbUser(respoUser);
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

    public static Observable getUsers() {
        final ArrayList<User> userList=new ArrayList<>();
        return Observable.fromCallable(new Callable<List<User>>() {
            @Override
            public List<User> call() throws Exception {
                try {
                    QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
                    pagedRequestBuilder.setPage(1);
                    pagedRequestBuilder.setPerPage(50);

                    List<QBUser> respoList = QBUsers.getUsers(pagedRequestBuilder).perform();
                    if(respoList!=null && respoList.size()>0){
                        for(QBUser user:respoList){
                            userList.add(User.fromQbUser(user));
                        }
                    }

                    UserDbHelper.getInstance().saveUserToDb(userList);

                    return userList;
                } catch (Exception e) {
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
}
