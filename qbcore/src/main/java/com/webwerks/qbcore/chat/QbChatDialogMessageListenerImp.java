package com.webwerks.qbcore.chat;

import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatMessage;

/**
 * Created by webwerks on 26/4/17.
 */

public class QbChatDialogMessageListenerImp implements QBChatDialogMessageListener {
    public QbChatDialogMessageListenerImp() {
    }

    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {

    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {

    }
}

