package com.webwerks.quickbloxdemo.chat;

import android.app.Activity;
import android.text.Editable;
import android.widget.EditText;
import android.widget.Toast;

import com.webwerks.qbcore.chat.ChatDialogManager;
import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.quickbloxdemo.databinding.ChatBinding;

import io.reactivex.functions.Consumer;


/**
 * Created by webwerks on 25/4/17.
 */

public class ChatViewModel {

    private ChatBinding chatBinding;
    private Activity mContext;
    private ChatDialog chatDialog;

    public ChatViewModel(Activity context, ChatBinding binding, ChatDialog dialog){
        chatBinding=binding;
        mContext=context;
        chatDialog=dialog;
    }

    public void onSendMsgClick(final EditText msg){
        Toast.makeText(mContext,"SEND: "+ msg.getText().toString(),Toast.LENGTH_SHORT).show();
        ChatDialogManager.sendMessage(chatDialog,msg.getText().toString()).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                msg.setText("");
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }

}
