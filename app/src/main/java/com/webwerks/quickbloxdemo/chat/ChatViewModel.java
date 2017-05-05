package com.webwerks.quickbloxdemo.chat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.widget.EditText;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.webwerks.qbcore.chat.ChatManager;
import com.webwerks.qbcore.chat.SendMessageRequest;
import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.qbcore.models.MessageType;
import com.webwerks.qbcore.models.Messages;
import com.webwerks.quickbloxdemo.chat.attachment.AttachmentDialog;
import com.webwerks.quickbloxdemo.databinding.ChatBinding;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.global.Constants;
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

    public ChatViewModel(Activity context, ChatBinding binding, ChatDialog dialog){
        chatBinding=binding;
        mContext=context;
        chatDialog=dialog;
    }

    public void onSendMsgClick(final EditText msg){
        if(!TextUtils.isEmpty(msg.getText())) {
            Observer progressObserver = new Observer<Integer>() {
                @Override
                public void onSubscribe(Disposable d) {
                }

                @Override
                public void onNext(Integer value) {
                }

                @Override
                public void onError(Throwable e) {
                }

                @Override
                public void onComplete() {
                }
            };

            SendMessageRequest sendRequest=new SendMessageRequest.Builder(chatDialog,progressObserver)
                    .message(msg.getText().toString())
                    .messageType(MessageType.TEXT).build();
            sendRequest.send().subscribe(new Consumer<Messages>() {
                @Override
                public void accept(Messages messages) throws Exception {
                    ((ChatActivity) mContext).showMessages(messages);
                    msg.setText("");
                }
            });

            /*ChatManager.getInstance().sendMessage(chatDialog, msg.getText().toString(),null,
                    null,ChatManager.AttachmentType.TEXT).subscribe(new Consumer() {
                @Override
                public void accept(Object o) throws Exception {
                    Messages chatMessages = (Messages) o;
                    ((ChatActivity) mContext).showMessages(chatMessages);
                    msg.setText("");
                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {

                }
            });*/
        }
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
                //mContext.startActivity(new Intent(mContext, SendLocationActivity.class));
            }
        });
        dialog.show();
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
