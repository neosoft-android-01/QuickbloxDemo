package com.webwerks.qbcore.chat;

import com.webwerks.qbcore.models.ChatMessages;

/**
 * Created by webwerks on 26/4/17.
 */

public interface IncomingMessageListener {

     void onMessageReceived(ChatMessages messages);
}
