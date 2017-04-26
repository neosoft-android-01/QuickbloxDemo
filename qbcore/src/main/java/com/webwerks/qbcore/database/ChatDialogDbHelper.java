package com.webwerks.qbcore.database;

import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.qbcore.models.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by webwerks on 24/4/17.
 */

public class ChatDialogDbHelper {

    Realm realmInstance=null;
    private static ChatDialogDbHelper instance;

    public static ChatDialogDbHelper getInstance(){
        if(instance==null)
            instance=new ChatDialogDbHelper();
        return instance;
    }

    public void saveDialogToDb(final ChatDialog chatDialog){
        try{
            realmInstance=Realm.getDefaultInstance();
            realmInstance.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    realm.copyToRealmOrUpdate(chatDialog);
                }
            });
        }finally {
            if(realmInstance!=null)
                realmInstance.close();
        }
    }

    public void saveDialogsToDb(final List<ChatDialog> chatDialogs){
        for(ChatDialog dialog:chatDialogs){
            saveDialogToDb(dialog);
        }
    }

    public Observable getAllDialogs(){

        return Observable.fromCallable(new Callable<List<ChatDialog>>() {
            @Override
            public List<ChatDialog> call() throws Exception {
                try {
                    realmInstance=Realm.getDefaultInstance();
                    realmInstance.beginTransaction();
                    ArrayList<ChatDialog> chatList=new ArrayList<>(realmInstance.where(ChatDialog.class).findAll());
                    realmInstance.cancelTransaction();
                    return chatList;

                }finally {
                    if(realmInstance!=null)
                        realmInstance.close();
                    //throw new Exception("");
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public Observable getDialog(final String dialogId){

        return Observable.fromCallable(new Callable<ChatDialog>() {
            @Override
            public ChatDialog call() throws Exception {
                try{
                    realmInstance=Realm.getDefaultInstance();
                    ChatDialog chatDialog=realmInstance.where(ChatDialog.class).equalTo("dialogId",dialogId).findFirst();
                    return realmInstance.copyFromRealm(chatDialog);
                }catch (Exception e){
                    e.printStackTrace();
                    throw new Exception(e.getLocalizedMessage());
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
