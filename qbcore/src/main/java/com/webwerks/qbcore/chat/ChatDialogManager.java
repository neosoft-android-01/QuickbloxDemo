package com.webwerks.qbcore.chat;

import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;

import java.util.ArrayList;
import java.util.concurrent.Callable;

import io.reactivex.Observable;

/**
 * Created by webwerks on 18/4/17.
 */

public class ChatDialogManager {

    public static Observable getRecentChatDialogs(){
        return Observable.fromCallable(new Callable<ArrayList<QBChatDialog>>() {
            @Override
            public ArrayList<QBChatDialog> call() throws Exception {

                QBRequestGetBuilder requestGetBuilder=new QBRequestGetBuilder();
                requestGetBuilder.setLimit(100);
                requestGetBuilder.setSkip(0);

                try {
                    return QBRestChatService.getChatDialogs(null,requestGetBuilder).perform();
                } catch (QBResponseException e) {
                    e.printStackTrace();
                    throw new Exception(e.getMessage());
                }





            }
        });
    }
}
