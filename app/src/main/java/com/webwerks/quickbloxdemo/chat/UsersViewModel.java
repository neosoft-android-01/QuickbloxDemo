package com.webwerks.quickbloxdemo.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.quickblox.chat.model.QBChatDialog;
import com.webwerks.qbcore.chat.ChatManager;
import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.qbcore.models.User;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.global.Constants;

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
        ChatManager.getInstance(mContext).createChatDialog(user).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                ChatDialog dialog = (ChatDialog) o;
                navigateNext(dialog);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
            }
        });
    }

    public void chatDialogClick(ChatDialog dialog){
        navigateNext(dialog);
    }

    public void navigateNext(ChatDialog dialog){
        Intent chatIntent=new Intent(mContext,ChatActivity.class);
        chatIntent.putExtra(Constants.EXTRA_DIALOG_ID,dialog.getDialogId());
        mContext.startActivity(chatIntent);
    }

}
