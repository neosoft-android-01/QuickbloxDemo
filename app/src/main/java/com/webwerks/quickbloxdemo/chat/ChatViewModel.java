package com.webwerks.quickbloxdemo.chat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.telecom.Call;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.webwerks.qbcore.chat.ChatDialogManager;
import com.webwerks.qbcore.chat.ChatManager;
import com.webwerks.qbcore.chat.SendMessageRequest;
import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.qbcore.models.MessageType;
import com.webwerks.qbcore.models.Messages;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.chat.attachment.AttachmentDialog;
import com.webwerks.quickbloxdemo.chat.audio.AudioRecodeManager;
import com.webwerks.quickbloxdemo.databinding.ChatBinding;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.global.Constants;
import com.webwerks.quickbloxdemo.ui.views.SlideToCancel.ViewProxy;
import com.webwerks.quickbloxdemo.utils.FileUtil;
import com.webwerks.quickbloxdemo.utils.PermissionManager;
import java.io.File;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;


/**
 * Created by webwerks on 25/4/17.
 */

public class ChatViewModel {

    private ChatBinding chatBinding;
    private Activity mContext;
    private ChatDialog chatDialog;
    private static File imageFile;
    File audioFile=null;

    public ChatViewModel(Activity context, ChatBinding binding, ChatDialog dialog){
        chatBinding=binding;
        mContext=context;
        chatDialog=dialog;
    }

    public void onAcceptClick(){
        ((ChatActivity) mContext).stopRingTone();
        mContext.startActivity(new Intent(mContext, CallActivity.class)
                .putExtra(Constants.EXTRA_IS_INCOMING_CALL,true));
        chatBinding.llCall.setVisibility(View.GONE);
    }

    public void onRejectCall(){
        ((ChatActivity) mContext).stopRingTone();
        ChatManager.rejectIncomingCall();
        chatBinding.llCall.setVisibility(View.GONE);
    }

    public void onSendMsgClick(final EditText msg){
        if(!TextUtils.isEmpty(msg.getText())) {
            SendMessageRequest sendRequest=new SendMessageRequest.Builder(chatDialog,null)
                    .message(msg.getText().toString())
                    .messageType(MessageType.TEXT).build();
            sendRequest.send().subscribe(new Consumer<Messages>() {
                @Override
                public void accept(Messages messages) throws Exception {
                    ((ChatActivity) mContext).showMessages(messages);
                    msg.setText("");
                }
            });
        }
    }

    public void leaveGroupClick(){
        ChatDialogManager.leaveGroup(chatDialog,App.getAppInstance().getCurrentUser().id).subscribe(new Consumer<Boolean>() {
            @Override
            public void accept(Boolean aBoolean) throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if(TextUtils.isEmpty(s)){
            chatBinding.btnSend.setVisibility(View.GONE);
            chatBinding.btnAudioRecord.setVisibility(View.VISIBLE);
        }else {
            chatBinding.btnSend.setVisibility(View.VISIBLE);
            chatBinding.btnAudioRecord.setVisibility(View.GONE);
        }
    }

    public static int dp(double value) {
        return (int) Math.ceil(1 * value);
    }

    private float startedDraggingX = -1;
    private float distCanMove = dp(dp(App.getAppInstance().getDisplayMatrix().widthPixels)*(0.50));

    public boolean onTouch(View view, MotionEvent motionEvent) {

        if (PermissionManager.askForPermission(2, mContext, Manifest.permission.RECORD_AUDIO, "")){

            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) chatBinding.slideText.getLayoutParams();
                params.leftMargin = dp(30);
                chatBinding.slideText.setLayoutParams(params);
                ViewProxy.setAlpha(chatBinding.slideText, 1);
                startedDraggingX = -1;

                startRecode();

                chatBinding.btnAudioRecord.getParent()
                        .requestDisallowInterceptTouchEvent(true);
                chatBinding.flAudioPanel.setVisibility(View.VISIBLE);
                chatBinding.llTextPanel.setVisibility(View.GONE);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_UP
                    || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
                startedDraggingX = -1;

                stopRecode();

                chatBinding.llTextPanel.setVisibility(View.VISIBLE);
                chatBinding.flAudioPanel.setVisibility(View.GONE);
            } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
                float x = motionEvent.getX();

                if (x < -distCanMove) {

                    cancelAudioSending();

                    chatBinding.llTextPanel.setVisibility(View.VISIBLE);
                    chatBinding.flAudioPanel.setVisibility(View.GONE);
                }
                x = x + ViewProxy.getX(chatBinding.btnAudioRecord);
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) chatBinding.slideText.getLayoutParams();
                if (startedDraggingX != -1) {
                    float dist = (x - startedDraggingX);
                    params.leftMargin = dp(30) + (int) dist;
                    chatBinding.slideText.setLayoutParams(params);
                    float alpha = 1.0f + dist / distCanMove;
                    if (alpha > 1) {
                        alpha = 1;
                    } else if (alpha < 0) {
                        alpha = 0;
                    }
                    ViewProxy.setAlpha(chatBinding.slideText, alpha);
                }
                if (x <= ViewProxy.getX(chatBinding.slideText) + chatBinding.slideText.getWidth() + dp(30)) {
                    if (startedDraggingX == -1) {
                        startedDraggingX = x;
                        distCanMove = (chatBinding.flAudioPanel.getMeasuredWidth()
                                - chatBinding.slideText.getMeasuredWidth() - dp(48)) / 2.0f;
                        if (distCanMove <= 0) {
                            distCanMove = dp(80);
                        } else if (distCanMove > dp(80)) {
                            distCanMove = dp(80);
                        }
                    }
                }
                if (params.leftMargin > dp(30)) {
                    params.leftMargin = dp(30);
                    chatBinding.slideText.setLayoutParams(params);
                    ViewProxy.setAlpha(chatBinding.slideText, 1);
                    startedDraggingX = -1;
                }
            }
            view.onTouchEvent(motionEvent);
        }
        return true;
    }

    Observer audioObserver;

    public void startRecode(){

        audioObserver=new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String value) {
                chatBinding.recordingTimeText.setText(value);
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                chatBinding.recordingTimeText.setText("00:00");
            }
        };

        audioFile=FileUtil.getAudioFile();
        AudioRecodeManager.getInstance().startRecode(audioFile,audioObserver);
    }

    public void stopRecode(){
        if(audioFile.exists()) {
            AudioRecodeManager.getInstance().stopRecode();
            if (chatBinding.recordingTimeText.getText().equals("00:00"))
                audioFile.delete();
            else
                ((ChatActivity) mContext).sendAttachment(audioFile, MessageType.AUDIO);
            audioObserver.onComplete();
        }
    }

    public void cancelAudioSending(){
        AudioRecodeManager.getInstance().stopRecode();
        audioFile.delete();
    }

    public void onAttachmentClick(){
        final AttachmentDialog dialog = new AttachmentDialog(mContext);
        dialog.setAttachmentListener(new AttachmentDialog.AttachmentListener() {
            @Override
            public void onPhotoCameraClick() {
                if (PermissionManager.askForPermissions(0, mContext,
                        new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                        "Do you want to access camera ?")) {
                    openCameraForPhoto();
                }
            }

            @Override
            public void onGalleryClick() {
                if (PermissionManager.askForPermission(2, mContext,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, "Do you want to access gallery ?")){
                    openGallery(mContext);
                }
            }

            @Override
            public void onLocationClick() {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    mContext.startActivityForResult(builder.build(mContext), Constants.PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAudioGalleryClick() {
                openAudioGallery(mContext);
            }
        });
        dialog.show();
    }

    private void openAudioGallery(Activity activity){
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(intent,Constants.GALLERY_AUDIO);
    }

    private void openGallery(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), Constants.GALLERY_IMAGE);
    }

    private void openCameraForPhoto() {
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (imageIntent.resolveActivity(mContext.getPackageManager()) != null) {
            imageFile = FileUtil.getImageFile();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(mContext,mContext.getApplication().getPackageName()+".provider", imageFile);
                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
            } else {
                imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageFile));
            }
            mContext.startActivityForResult(imageIntent,Constants.CAMERA_IMAGE);
        }

    }

    public static File getImageFile(){
        return imageFile;
    }

}
