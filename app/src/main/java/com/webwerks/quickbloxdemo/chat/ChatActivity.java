package com.webwerks.quickbloxdemo.chat;

import android.widget.ListView;

import com.webwerks.qbcore.chat.ChatDialogManager;
import com.webwerks.qbcore.chat.ChatManager;
import com.webwerks.qbcore.chat.IncomingMessageListener;
import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.qbcore.models.ChatMessages;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.databinding.ChatBinding;
import com.webwerks.quickbloxdemo.global.Constants;
import com.webwerks.quickbloxdemo.ui.activities.BaseActivity;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * Created by webwerks on 17/4/17.
 */

public class ChatActivity extends BaseActivity<ChatBinding> implements IncomingMessageListener {

    ChatDialog currentDialog;
    ChatAdapterList adapter;
    ArrayList<ChatMessages> messages;

    @Override
    public int getContentLayout() {
        return R.layout.activity_chat;
    }

    @Override
    public void initializeUiComponents(final ChatBinding binding) {
        String dialogId=getIntent().getStringExtra(Constants.EXTRA_DIALOG_ID);

        ChatDialogManager.getDialogFromId(dialogId).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                currentDialog= (ChatDialog) o;
                ChatManager.getInstance().initSession(currentDialog,ChatActivity.this);

                ChatDialogManager.getDialogMessages(currentDialog).subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        messages = (ArrayList<ChatMessages>) o;
                        binding.setViewModel(new ChatViewModel(ChatActivity.this,binding,currentDialog));

                         adapter = new ChatAdapterList(ChatActivity.this,messages);
                        ((ListView)findViewById(R.id.lst_chat)).setAdapter(adapter);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });

            }
        });
    }

    public void scrollMessageListDown(){
        ((ListView)findViewById(R.id.lst_chat)).smoothScrollToPosition(messages.size()-1);
    }

    public void showMessages(ChatMessages msg){
        if (adapter != null) {
            adapter.add(msg);
            scrollMessageListDown();
        }
    }

    @Override
    public void onMessageReceived(ChatMessages messages) {
        showMessages(messages);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatManager.getInstance().stopSession(currentDialog);
    }
}
