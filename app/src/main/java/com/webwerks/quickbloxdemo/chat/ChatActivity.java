package com.webwerks.quickbloxdemo.chat;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.quickblox.chat.model.QBAttachment;
import com.webwerks.qbcore.chat.ChatDialogManager;
import com.webwerks.qbcore.chat.ChatManager;
import com.webwerks.qbcore.chat.CurrentCallStateCallback;
import com.webwerks.qbcore.chat.IncomingMessageListener;
import com.webwerks.qbcore.chat.SendMessageRequest;
import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.qbcore.models.MessageType;
import com.webwerks.qbcore.models.Messages;
import com.webwerks.qbcore.models.Presence;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.chat.audio.AudioPlayerManager;
import com.webwerks.quickbloxdemo.chat.location.UploadLocation;
import com.webwerks.quickbloxdemo.databinding.ChatBinding;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.global.Constants;
import com.webwerks.quickbloxdemo.ui.activities.BaseActivity;
import com.webwerks.quickbloxdemo.utils.FileUtil;
import com.webwerks.quickbloxdemo.utils.RingtonePlayer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

/**
 * Created by webwerks on 17/4/17.
 */

public class ChatActivity extends BaseActivity<ChatBinding> implements IncomingMessageListener,View.OnClickListener {

    ChatDialog currentDialog;
    ChatAdapter adapter;
    ArrayList<Messages> messages;
    RecyclerView lstChat;
    RingtonePlayer ringtonePlayer;

    @Override
    public int getContentLayout() {
        return R.layout.activity_chat;
    }

    @Override
    public void initializeUiComponents(final ChatBinding binding) {

        lstChat = ((RecyclerView) findViewById(R.id.lst_chat));
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar_view);
        if(toolbar!=null) {
            toolbar.findViewById(R.id.btn_call).setVisibility(View.VISIBLE);
            toolbar.findViewById(R.id.btn_call).setOnClickListener(this);
        }

        String dialogId = getIntent().getStringExtra(Constants.EXTRA_DIALOG_ID);

        ChatDialogManager.getDialogFromId(dialogId).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                currentDialog = (ChatDialog) o;

                if(currentDialog.getType()== ChatDialog.DialogType.GROUP)
                    binding.btnLeaveGrp.setVisibility(View.VISIBLE);
                else
                    binding.btnLeaveGrp.setVisibility(View.GONE);

                ChatManager.getInstance().initSession(ChatActivity.this,currentDialog, ChatActivity.this);

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
    public void onIncomingCall() {
        if(ChatManager.getInstance().checkUserPresence(App.getAppInstance().getCurrentUser().id)
                .equals(Presence.ONLINE)) {

            ringtonePlayer = new RingtonePlayer(this, R.raw.beep);
            ringtonePlayer.play(true);
            findViewById(R.id.ll_call).setVisibility(View.VISIBLE);
            ChatManager.setCurrentCallStateCallback(new CurrentCallStateCallback() {
                @Override
                public void onCallStarted() {
                }

                @Override
                public void onCallStopped() {
                    ringtonePlayer.stop();
                    findViewById(R.id.ll_call).setVisibility(View.GONE);
                }

                @Override
                public void onCallConnectionClose(int callerId) {
                    ringtonePlayer.stop();
                    findViewById(R.id.ll_call).setVisibility(View.GONE);
                }
            });
        }
    }

    public void stopRingTone(){
        if(ringtonePlayer!=null)
            ringtonePlayer.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
        ChatManager.getInstance().stopSession(this,currentDialog);
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

                case Constants.CALL:
                    if (data != null) {
                        boolean caller=data.getBooleanExtra("CALLER",false);
                        final String duration = data.getStringExtra("DURATION");

                        Log.e("CALL INFO",caller+":::"+duration);

                        if(caller){
                            SendMessageRequest sendRequest = new SendMessageRequest.Builder(currentDialog, null)
                                    .messageType(MessageType.CALL)
                                    .setCallDuration(duration).build();
                            sendRequest.send().subscribe(new Consumer<Messages>() {
                                @Override
                                public void accept(Messages messages) throws Exception {
                                    showMessages(messages);
                                }
                            });
                        }
                    }
                    break;
            }
        }
    }

    public void sendAttachment(final File filePath,final MessageType type) {
        if (filePath != null) {
            Single.just(new Messages()).map(new Function<Messages, Messages>() {
                @Override
                public Messages apply(Messages msg) throws Exception {
                    msg.setChatDialogId(currentDialog.getDialogId());
                    msg.setId(App.getAppInstance().getCurrentUser().id+"");

                    QBAttachment qbAttachment=new QBAttachment(QBAttachment.IMAGE_TYPE);
                    qbAttachment.setContentType(QBAttachment.IMAGE_TYPE);
                    qbAttachment.setUrl(filePath.getPath());

                    List<QBAttachment> attachments=new ArrayList<>();
                    attachments.add(qbAttachment);
                    msg.setMessageType(type);
                    msg.setAttachments(attachments);
                    msg.setDateSent(System.currentTimeMillis() / 1000);
                    return msg;
                }
            }).subscribe(new Consumer<Messages>() {
                @Override
                public void accept(final Messages m) throws Exception {
                    m.setInProgress(true);
                    showMessages(m);
                    SendMessageRequest sendRequest = new SendMessageRequest.Builder(currentDialog, null)
                            .setTime(m.getDateSent())
                            .attachMedia(filePath)
                            .messageType(type).build();
                    sendRequest.send().subscribe(new Consumer<Messages>() {
                        @Override
                        public void accept(Messages messages) throws Exception {
                            m.setInProgress(false);
                            m.setId(messages.getId());
                            m.setChatDialogId(messages.getChatDialogId());
                            if(messages.getDeliveredIds()!=null)
                                m.setDateSent(messages.getDateSent());
                            if(messages.getReadIds()!=null)
                                m.setDeliveredIds(messages.getDeliveredIds());
                            m.setReadIds(messages.getReadIds());
                            m.setMsg(messages.getMsg());
                            m.setRecipientId(messages.getRecipientId());
                            m.setSenderId(messages.getSenderId());
                            m.setAttachments(messages.getAttachments());
                            m.setLocationAttachment(messages.getLocationAttachment());
                            m.setMessageType(messages.getMessageType());

                            updateMessageData();
                        }
                    });
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {
                }
            });

        }
    }

    public void updateMessageData(){
        //adapter.update(oldMessage);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AudioPlayerManager.getInstance().release();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_call:
                ChatManager.startCall(this,currentDialog.getOccupantsId());
                startActivityForResult(new Intent(this, CallActivity.class)
                        .putExtra(Constants.EXTRA_IS_INCOMING_CALL,false),Constants.CALL);
                break;
        }
    }
}
