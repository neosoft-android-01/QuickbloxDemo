package com.webwerks.quickbloxdemo.chat;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.quickblox.chat.model.QBChatDialog;
import com.webwerks.qbcore.chat.ChatManager;
import com.webwerks.qbcore.models.User;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * Created by webwerks on 17/4/17.
 */

public class UsersViewModel {

    private Context mContext;

    public UsersViewModel(Context activity){
        mContext=activity;
    }

    public void onUserClick(User user){
        //new ChatActivity().start();

        ChatManager.getRecentChatList().subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                ArrayList<QBChatDialog> dialogs= (ArrayList<QBChatDialog>) o;
                for(QBChatDialog dialog:dialogs){
                    Log.e("Dialog",dialog.getName() + "::::");
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }

}
