package com.webwerks.qbcore.models;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.webwerks.qbcore.utils.RealmHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by webwerks on 24/4/17.
 */

public class ChatDialog extends RealmObject {

    public ChatDialog(){}

    @PrimaryKey
    private String dialogId;
    private Date createdAt;
    private String lastMsg;
    private long lastMsgSentDate;
    private Integer lastMsgUserId;
    private String name;
    private RealmList<RealmInteger> occupantsId;
    private String photo;
    private Integer type;
    private Date updatedAt;
    private Integer userId;
    private String xmpp_room_jid;
    private Integer unreadMsgCount;

    public String getDialogId() {
        return dialogId;
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public long getLastMsgSentDate() {
        return lastMsgSentDate;
    }

    public void setLastMsgSentDate(long lastMsgSentDate) {
        this.lastMsgSentDate = lastMsgSentDate;
    }

    public Integer getLastMsgUserId() {
        return lastMsgUserId;
    }

    public void setLastMsgUserId(Integer lastMsgUserId) {
        this.lastMsgUserId = lastMsgUserId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public RealmList<RealmInteger> getOccupantsId() {
        return occupantsId;
    }

    public void setOccupantsId(RealmList<RealmInteger> occupantsId) {
        this.occupantsId = occupantsId;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public DialogType getType() {
        if(type==1)
            return DialogType.ONE_TO_ONE;
        else
            return DialogType.GROUP;

    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getXmpp_room_jid() {
        return xmpp_room_jid;
    }

    public void setXmpp_room_jid(String xmpp_room_jid) {
        this.xmpp_room_jid = xmpp_room_jid;
    }

    public Integer getUnreadMsgCount() {
        return unreadMsgCount;
    }

    public void setUnreadMsgCount(Integer unreadMsgCount) {
        this.unreadMsgCount = unreadMsgCount;
    }

    public static ChatDialog createChatDialog(QBChatDialog chatDialog){
        ChatDialog currentDialog=new ChatDialog();
        currentDialog.dialogId=chatDialog.getDialogId();
        currentDialog.createdAt=chatDialog.getCreatedAt();
        currentDialog.lastMsg=chatDialog.getLastMessage();
        currentDialog.lastMsgSentDate=chatDialog.getLastMessageDateSent();
        currentDialog.lastMsgUserId=chatDialog.getLastMessageUserId();
        currentDialog.name=chatDialog.getName();
        currentDialog.occupantsId= RealmHelper.getIntegerRelamList(chatDialog.getOccupants());
        currentDialog.photo=chatDialog.getPhoto();

        if(chatDialog.getType()== QBDialogType.PRIVATE){
            currentDialog.type=1;
        }else if(chatDialog.getType()==QBDialogType.GROUP){
            currentDialog.type=2;
        }

        currentDialog.updatedAt=chatDialog.getUpdatedAt();
        currentDialog.userId=chatDialog.getUserId();
        currentDialog.unreadMsgCount=chatDialog.getUnreadMessageCount();

        return currentDialog;
    }

    public static QBChatDialog toQbChatDialog(ChatDialog chatDialog){
        QBChatDialog qbChatDialog=new QBChatDialog();
        qbChatDialog.setDialogId(chatDialog.dialogId);
        qbChatDialog.setCreatedAt(chatDialog.createdAt);
        qbChatDialog.setLastMessage(chatDialog.lastMsg);
        qbChatDialog.setLastMessageDateSent(chatDialog.lastMsgSentDate);
        qbChatDialog.setLastMessageUserId(chatDialog.lastMsgUserId);
        qbChatDialog.setName(chatDialog.name);
        qbChatDialog.setOccupantsIds(RealmHelper.fromIntegerRelamList(chatDialog.getOccupantsId()));
        qbChatDialog.setPhoto(chatDialog.photo);

        if(chatDialog.getType()== DialogType.ONE_TO_ONE){
            qbChatDialog.setType(QBDialogType.PRIVATE);
        }else if(chatDialog.getType()==DialogType.GROUP){
            qbChatDialog.setType(QBDialogType.GROUP);
        }

        qbChatDialog.setUpdatedAt(chatDialog.updatedAt);
        qbChatDialog.setUserId(chatDialog.userId);
        qbChatDialog.setUnreadMessageCount(chatDialog.unreadMsgCount);

        return qbChatDialog;
    }



    enum DialogType{
        GROUP,
        ONE_TO_ONE;
    }
}
