package com.webwerks.quickbloxdemo.chat;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.webwerks.qbcore.chat.ChatDialogManager;
import com.webwerks.qbcore.chat.ChatManager;
import com.webwerks.qbcore.chat.IncomingMessageListener;
import com.webwerks.qbcore.chat.SendMessageRequest;
import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.qbcore.models.MessageType;
import com.webwerks.qbcore.models.Messages;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.chat.audio.AudioPlayerManager;
import com.webwerks.quickbloxdemo.chat.location.UploadLocation;
import com.webwerks.quickbloxdemo.databinding.ChatBinding;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.global.Constants;
import com.webwerks.quickbloxdemo.ui.activities.BaseActivity;
import com.webwerks.quickbloxdemo.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Created by webwerks on 17/4/17.
 */

public class ChatActivity extends BaseActivity<ChatBinding> implements IncomingMessageListener {

    ChatDialog currentDialog;
    ChatAdapter adapter;
    ArrayList<Messages> messages;
    RecyclerView lstChat;

    @Override
    public int getContentLayout() {
        return R.layout.activity_chat;
    }

    @Override
    public void initializeUiComponents(final ChatBinding binding) {

        lstChat = ((RecyclerView) findViewById(R.id.lst_chat));
        String dialogId = getIntent().getStringExtra(Constants.EXTRA_DIALOG_ID);

        ChatDialogManager.getDialogFromId(dialogId).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                currentDialog = (ChatDialog) o;
                ChatManager.getInstance().initSession(currentDialog, ChatActivity.this);

                App.getAppInstance().showLoading(ChatActivity.this);
                ChatDialogManager.getDialogMessages(currentDialog).subscribe(new Consumer() {
                    @Override
                    public void accept(Object o) throws Exception {
                        App.getAppInstance().hideLoading();
                        messages = (ArrayList<Messages>) o;
                        binding.setViewModel(new ChatViewModel(ChatActivity.this, binding, currentDialog));

                        adapter = new ChatAdapter(ChatActivity.this, messages);
                        lstChat.setAdapter(adapter);
                        lstChat.scrollToPosition(messages.size() - 1);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                    }
                });

            }
        });
    }

    public void scrollMessageListDown() {
        lstChat.smoothScrollToPosition(messages.size() - 1);
    }

    public void showMessages(Messages msg) {
        if (adapter != null && !adapter.messages.contains(msg)) {
            adapter.add(msg);
            scrollMessageListDown();
        }
    }

    @Override
    public void onMessageReceived(Messages messages) {
        showMessages(messages);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ChatManager.getInstance().stopSession(currentDialog);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            File imagePath = null;
            switch (requestCode) {
                case Constants.CAMERA_IMAGE:
                    imagePath = ChatViewModel.getImageFile();
                    sendAttachment(imagePath,MessageType.IMAGE);
                    break;

                case Constants.GALLERY_IMAGE:
                    if (requestCode == Constants.GALLERY_IMAGE && data != null && data.getData() != null) {
                        FileUtil.MediaData mediaData = FileUtil.getPath(this, data.getData());

                        if (mediaData != null && !TextUtils.isEmpty(mediaData.getType())
                                && !TextUtils.isEmpty(mediaData.getPath())) {
                            if (mediaData.getType().contains("image")) {
                                imagePath = new File(mediaData.getPath());
                                sendAttachment(imagePath,MessageType.IMAGE);
                            }
                        }
                    }
                    break;

                case Constants.PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(data, this);
                    UploadLocation.sendLocation(this,currentDialog,place);
                    break;

                case Constants.GALLERY_AUDIO:
                    FileUtil.MediaData mediaData = FileUtil.getPath(this, data.getData());
                    if (mediaData != null) {
                        Log.e("PATH", mediaData.getPath() + "::::");
                        sendAttachment(new File(mediaData.getPath()),MessageType.AUDIO);
                    }
                    break;
            }
        }
    }

    public void sendAttachment(File filePath,MessageType type) {
        if (filePath != null) {
            App.getAppInstance().showLoading(this);

            SendMessageRequest sendRequest=new SendMessageRequest.Builder(currentDialog,null)
                .attachMedia(filePath)
                .messageType(type).build();
            sendRequest.send().subscribe(new Consumer<Messages>() {
                @Override
                public void accept(Messages messages) throws Exception {
                    showMessages(messages);
                    App.getAppInstance().hideLoading();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AudioPlayerManager.getInstance().release();
    }
}
