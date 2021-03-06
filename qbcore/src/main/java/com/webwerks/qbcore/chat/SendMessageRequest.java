package com.webwerks.qbcore.chat;

import android.os.Environment;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.content.model.QBFile;
import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.qbcore.models.LocationAttachment;
import com.webwerks.qbcore.models.MessageType;
import com.webwerks.qbcore.models.Messages;
import com.webwerks.qbcore.utils.Constant;

import org.jivesoftware.smack.SmackException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by webwerks on 5/5/17.
 */

public class SendMessageRequest {

    private final ChatDialog chatDialog;
    private final String text;
    private final List<File> mediaAttachment;
    private final LocationAttachment locationAttachment;
    private final MessageType type;
    private Observer<Integer> progressUpdate;
    private final long sentTime;
    private final String callDuration;

    private SendMessageRequest(Builder builder){
        this.chatDialog=builder.chatDialog;
        this.text=builder.text;
        this.mediaAttachment=builder.mediaAttachment;
        this.locationAttachment=builder.locationAttachment;
        this.type=builder.type;
        this.sentTime=builder.sentTime;
        this.progressUpdate=builder.progressUpdate;
        this.callDuration=builder.callDuration;
    }

    public static class Builder{

        private ChatDialog chatDialog;
        private String text;
        private List<File> mediaAttachment;
        private LocationAttachment locationAttachment;
        private MessageType type;
        private Observer<Integer> progressUpdate;
        private long sentTime;
        private String callDuration;

        public Builder(ChatDialog dialog,Observer<Integer> progressUpdate) {
            this.chatDialog = dialog;
            this.progressUpdate=progressUpdate;
        }

        public Builder message(String msg){
            this.text=msg;
            return this;
        }

        public Builder attachMedia(File file){
            if(mediaAttachment==null)
                mediaAttachment=new ArrayList<>();
            mediaAttachment.add(file);
            return this;
        }

        public Builder attachLocation(LocationAttachment locationAttachment){
            this.locationAttachment=locationAttachment;
            return this;
        }

        public Builder messageType(MessageType type){
            this.type=type;
            return this;
        }

        public Builder setTime(long sentTime){
            this.sentTime=sentTime;
            return this;
        }

        public Builder setCallDuration(String duration){
            this.callDuration=duration;
            return this;
        }

        public SendMessageRequest build() {
            return new SendMessageRequest(this);
        }
    }

    public Observable<Messages> send(){
        try {
            if (mediaAttachment != null && mediaAttachment.size()>0) {
                return AttachmentManager.uploadMediaAttachment(mediaAttachment.get(0),progressUpdate)
                        .concatMap(new Function<QBFile, ObservableSource<Messages>>() {
                            @Override
                            public ObservableSource<Messages> apply(QBFile qbFile) throws Exception {

                                String attachmentType="";
                                switch (type){
                                    case IMAGE:
                                    case LOCATION:
                                        if(mediaAttachment.get(0).getPath().contains("PocketLocal")) {
                                            File imageFile = new File(Environment.getExternalStorageDirectory().getPath()
                                                    + "/PocketLocal/image" +  File.separator + "IMG_" + qbFile.getId()  + ".png");
                                            boolean success = mediaAttachment.get(0).renameTo(imageFile);
                                            mediaAttachment.get(0).delete();
                                        }
                                        attachmentType=QBAttachment.IMAGE_TYPE;
                                        break;

                                    case VIDEO:
                                        attachmentType=QBAttachment.VIDEO_TYPE;
                                        break;

                                    case AUDIO:
                                        if(mediaAttachment.get(0).getPath().contains("PocketLocal")) {
                                            File audioFile = new File(Environment.getExternalStorageDirectory().getPath()
                                                    + "/PocketLocal/audio" + File.separator + "AUD_" + qbFile.getId() + ".mp3");
                                            boolean success = mediaAttachment.get(0).renameTo(audioFile);
                                            mediaAttachment.get(0).delete();
                                        }
                                        attachmentType=QBAttachment.AUDIO_TYPE;
                                        break;
                                }

                                QBAttachment attachment = new QBAttachment(attachmentType);
                                attachment.setId(qbFile.getId().toString());
                                attachment.setUrl(qbFile.getPrivateUrl());
                                return sendMessage(attachment)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread());
                            }
                        });
            } else {
                return sendMessage(null)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private Observable<Messages> sendMessage(final QBAttachment qbAttachment){
        return Observable.fromCallable(new Callable<Messages>() {
            @Override
            public Messages call() throws Exception {
                try {
                    QBChatDialog qbChatDialog=ChatDialog.toQbChatDialog(chatDialog);
                    qbChatDialog.initForChat(QBChatService.getInstance());

                    QBChatMessage chatMessage = new QBChatMessage();

                    if(mediaAttachment!=null){
                        chatMessage.addAttachment(qbAttachment);
                    }else{
                        chatMessage.setBody(text);
                    }

                    if(type== MessageType.LOCATION) {
                        chatMessage.setBody(Constant.CHAT_SHARE_LOCATION_MSG_BODY);
                        chatMessage.setProperty(Constant.QB_CUSTOM_LOCATION_LAT, String.valueOf(locationAttachment.getLatitude()));
                        chatMessage.setProperty(Constant.QB_CUSTOM_LOCATION_LNG, String.valueOf(locationAttachment.getLongitude()));
                        chatMessage.setProperty(Constant.QB_CUSTOM_LOCATION_NAME, locationAttachment.getLocationName());
                        chatMessage.setProperty(Constant.QB_CUSTOM_LOCATION_DESC, locationAttachment.getLocationDesc());
                        chatMessage.setProperty(Constant.QB_CUSTOM_LOCATION_MAP_IMG,locationAttachment.getUrl());
                    }

                    if(type==MessageType.CALL){
                        chatMessage.setBody(Constant.CHAT_CALL);
                        chatMessage.setProperty(Constant.QB_CUSTOM_CALL_DURATION,callDuration);
                    }
                    chatMessage.setSaveToHistory(true); // Save a message to history
                    chatMessage.setDateSent(sentTime==0?System.currentTimeMillis() / 1000:sentTime);
                    chatMessage.setMarkable(true);
                    chatMessage.setSenderId(chatDialog.getUserId());
                    qbChatDialog.sendMessage(chatMessage);
                    return Messages.getChatMessage(chatMessage);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                    throw new Exception(e.getLocalizedMessage());
                }
            }
        });
    }

}
