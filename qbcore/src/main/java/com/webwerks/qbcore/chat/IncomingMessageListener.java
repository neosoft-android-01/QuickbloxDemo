package com.webwerks.qbcore.chat;

import com.webwerks.qbcore.models.Messages;

/**
 * Created by webwerks on 26/4/17.
 */

public interface IncomingMessageListener {
     void onMessageReceived(Messages messages);
}
