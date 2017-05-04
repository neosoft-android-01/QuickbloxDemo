package com.webwerks.quickbloxdemo.chat;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.webwerks.qbcore.chat.ChatDialogManager;
import com.webwerks.qbcore.chat.ChatManager;
import com.webwerks.qbcore.chat.IncomingMessageListener;
import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.qbcore.models.ChatMessages;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.chat.location.UploadLocation;
import com.webwerks.quickbloxdemo.databinding.ChatBinding;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.global.Constants;
import com.webwerks.quickbloxdemo.ui.activities.BaseActivity;
import com.webwerks.quickbloxdemo.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

/**
 * Created by webwerks on 17/4/17.
 */

public class ChatActivity extends BaseActivity<ChatBinding> implements IncomingMessageListener {

    ChatDialog currentDialog;
    ChatAdapter adapter;
    ArrayList<ChatMessages> messages;
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
                        messages = (ArrayList<ChatMessages>) o;
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

    public void showMessages(ChatMessages msg) {
        if (adapter != null && !adapter.messages.contains(msg)) {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            File imagePath = null;
            switch (requestCode) {
                case Constants.CAMERA_IMAGE:
                    imagePath = ChatViewModel.getImageFile();
                    sendImage(imagePath);
                    break;

                case Constants.GALLERY_IMAGE:
                    if (requestCode == Constants.GALLERY_IMAGE && data != null && data.getData() != null) {
                        FileUtil.MediaData mediaData = FileUtil.getPath(this, data.getData());

                        if (mediaData != null && !TextUtils.isEmpty(mediaData.getType())
                                && !TextUtils.isEmpty(mediaData.getPath())) {
                            if (mediaData.getType().contains("image")) {
                                imagePath = new File(mediaData.getPath());
                                sendImage(imagePath);
                            }
                        }
                    }
                    break;

                case Constants.PLACE_PICKER_REQUEST:
                    Place place = PlacePicker.getPlace(data, this);
                    /*String toastMsg = String.format("Place: %s", place.getName());
                    Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();*/
                    UploadLocation.sendLocation(this,currentDialog,place);
                    break;
            }

        }

    }


    public void sendImage(File imagePath) {
        if (imagePath != null) {
            App.getAppInstance().showLoading(this);
            ChatManager.getInstance().sendMessage(currentDialog, "", imagePath, null,ChatManager.AttachmentType.IMAGE).subscribe(new Consumer() {
                @Override
                public void accept(Object o) throws Exception {
                    ChatMessages chatMessages = (ChatMessages) o;
                    showMessages(chatMessages);
                    App.getAppInstance().hideLoading();
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {

                }
            });
        }
    }
}
