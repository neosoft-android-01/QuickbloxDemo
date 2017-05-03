package com.webwerks.qbcore.models;

import com.quickblox.chat.model.QBAttachment;
import com.quickblox.chat.model.QBChatMessage;
import com.webwerks.qbcore.utils.RealmHelper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by webwerks on 25/4/17.
 */

public class ChatMessages {

    private String id;
    private String chatDialogId;
    private Date createdAt;
    private long dateSent;
    private String msg;
    private Integer recipientId;
    private Integer senderId;
    private Date updatedAt;
    private int read;
    private RealmList<RealmInteger> deliveredIds;
    private RealmList<RealmInteger> readIds;
    private RealmList<RealmAttachment> attachments;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatDialogId() {
        return chatDialogId;
    }

    public void setChatDialogId(String chatDialogId) {
        this.chatDialogId = chatDialogId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public long getDateSent() {
        return dateSent;
    }

    public void setDateSent(long dateSent) {
        this.dateSent = dateSent;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Integer getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Integer recipientId) {
        this.recipientId = recipientId;
    }

    public Integer getSenderId() {
        return senderId;
    }

    public void setSenderId(Integer senderId) {
        this.senderId = senderId;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public int getRead() {
        return read;
    }

    public void setRead(int read) {
        this.read = read;
    }

    public List<Integer> getDeliveredIds() {
        return RealmHelper.fromIntegerRelamList(deliveredIds);
    }

    public void setDeliveredIds(List<Integer> deliveredIds) {
        this.deliveredIds = RealmHelper.getIntegerRelamList(deliveredIds);
    }

    public List<Integer> getReadIds() {
        return RealmHelper.fromIntegerRelamList(readIds);
    }

    public void setReadIds(List<Integer> readIds) {
        this.readIds = RealmHelper.getIntegerRelamList(readIds);
    }

    public List<QBAttachment> getAttachments() {

        if(attachments!=null) {

            List<QBAttachment> attachmentList = new ArrayList<>();
            for (RealmAttachment attachment : attachments) {
                attachmentList.add(RealmAttachment.getQBAttachment(attachment));
            }
            return attachmentList;
        }else{
            return new ArrayList<>();
        }
    }

    public void setAttachments(List<QBAttachment> attachments) {

        RealmList<RealmAttachment> attachmentList=new RealmList<>();
        for(QBAttachment attachment:attachments){
            attachmentList.add(RealmAttachment.getRealmAttachment(attachment));
        }

        this.attachments = attachmentList;
    }

    public static QBChatMessage getQBChatMessage(ChatMessages messages){
        QBChatMessage qbChatMessage=new QBChatMessage();
        qbChatMessage.setId(messages.getId());
        qbChatMessage.setDialogId(messages.getChatDialogId());
        qbChatMessage.setDateSent(messages.getDateSent());
        qbChatMessage.setDeliveredIds(messages.getDeliveredIds());
        qbChatMessage.setReadIds(messages.getReadIds());
        qbChatMessage.setBody(messages.getMsg());
        qbChatMessage.setRecipientId(messages.getRecipientId());
        qbChatMessage.setSenderId(messages.getSenderId());
        qbChatMessage.setAttachments(messages.getAttachments());
        return qbChatMessage;
    }

    public static ChatMessages getChatMessage(QBChatMessage qbChatMessage){
        ChatMessages chatMessages=new ChatMessages();
        chatMessages.setId(qbChatMessage.getId());
        chatMessages.setChatDialogId(qbChatMessage.getDialogId());
        chatMessages.setDateSent(qbChatMessage.getDateSent());
        if(qbChatMessage.getDeliveredIds()!=null)
            chatMessages.setDeliveredIds((List<Integer>) qbChatMessage.getDeliveredIds());
        if(qbChatMessage.getReadIds()!=null)
            chatMessages.setReadIds((List<Integer>) qbChatMessage.getReadIds());
        chatMessages.setMsg(qbChatMessage.getBody());
        chatMessages.setRecipientId(qbChatMessage.getRecipientId());
        chatMessages.setSenderId(qbChatMessage.getSenderId());
        if(qbChatMessage.getAttachments()!=null)
            chatMessages.setAttachments((List<QBAttachment>) qbChatMessage.getAttachments());
        return chatMessages;
    }


}
