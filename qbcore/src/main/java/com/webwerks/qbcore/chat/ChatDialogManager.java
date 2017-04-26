package com.webwerks.qbcore.chat;

import android.content.Context;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;
import com.webwerks.qbcore.database.ChatDialogDbHelper;
import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.qbcore.models.ChatMessages;
import com.webwerks.qbcore.utils.NetworkUtils;

import org.jivesoftware.smack.SmackException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by webwerks on 18/4/17.
 */

public class ChatDialogManager {

    public static Observable getRecentChatDialogs(final Context context) {
        return Observable.fromCallable(new Callable<List<ChatDialog>>() {
            @Override
            public List<ChatDialog> call() throws Exception {

                QBRequestGetBuilder requestGetBuilder = new QBRequestGetBuilder();
                requestGetBuilder.setLimit(100);
                requestGetBuilder.setSkip(0);

                try {
                    if (NetworkUtils.isConnected(context)) {
                        List<ChatDialog> dialogList = new ArrayList<ChatDialog>();
                        ArrayList<QBChatDialog> chatDialogs = QBRestChatService.getChatDialogs(null, requestGetBuilder).perform();
                        if (chatDialogs != null && chatDialogs.size() > 0) {
                            for (QBChatDialog dialog : chatDialogs) {
                                dialogList.add(ChatDialog.createChatDialog(dialog));
                            }
                        }
                        ChatDialogDbHelper.getInstance().saveDialogsToDb(dialogList);

                        return dialogList;
                    } else {

/*
                        ChatDialogDbHelper.getInstance().getAllDialogs().subscribe(new Consumer() {
                            @Override
                            public void accept(Object o) throws Exception {
                                List<ChatDialog> dialogList = new ArrayList<ChatDialog>();
                                dialogList= (List<ChatDialog>) o;
                            }
                        });
*/

                        return new ArrayList<ChatDialog>();
                    }
                } catch (QBResponseException e) {
                    e.printStackTrace();
                    throw new Exception(e.getMessage());
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static Observable createChatDialogFromUser(final QBUser user) {
        return Observable.fromCallable(new Callable<ChatDialog>() {
            @Override
            public ChatDialog call() throws Exception {
                try {
                    QBChatDialog dialog = QBRestChatService.createChatDialog(createDialog(user)).perform();
                    ChatDialog chatDialog = ChatDialog.createChatDialog(dialog);
                    ChatDialogDbHelper.getInstance().saveDialogToDb(chatDialog);
                    return chatDialog;
                } catch (QBResponseException e) {
                    e.printStackTrace();
                    throw new Exception(e.getLocalizedMessage());
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static QBChatDialog createDialog(QBUser qbUser) {
        return DialogUtils.buildDialog(qbUser);
    }

    public static Observable getDialogMessages(final ChatDialog dialog) {

        return Observable.fromCallable(new Callable<List<ChatMessages>>() {
            @Override
            public List<ChatMessages> call() throws Exception {
                QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
                messageGetBuilder.setLimit(500);

                try {
                    QBChatDialog qbChatDialog=ChatDialog.toQbChatDialog(dialog);
                    ArrayList<QBChatMessage> chatMessages=QBRestChatService.getDialogMessages(qbChatDialog, messageGetBuilder).perform();

                    List<ChatMessages> messages=new ArrayList<ChatMessages>();
                    for(QBChatMessage chatMessage:chatMessages){
                        messages.add(ChatMessages.getChatMessage(chatMessage));
                    }

                    return messages;
                } catch (QBResponseException e) {
                    e.printStackTrace();
                    throw new Exception(e.getLocalizedMessage());
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public static Observable sendMessage(final ChatDialog dialog, final String msg) {

        return Observable.fromCallable(new Callable() {
            @Override
            public ChatMessages call() throws Exception {
                try {
                    QBChatDialog chatDialog=ChatDialog.toQbChatDialog(dialog);
                    chatDialog.initForChat(QBChatService.getInstance());

                    QBChatMessage chatMessage = new QBChatMessage();
                    chatMessage.setBody(msg);
                    chatMessage.setProperty("save_to_history", "1");
                    chatMessage.setDateSent(System.currentTimeMillis() / 1000);
                    chatMessage.setMarkable(true);
                    chatDialog.sendMessage(chatMessage);
                    return ChatMessages.getChatMessage(chatMessage);
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                    throw new Exception(e.getLocalizedMessage());
                }
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    public static Observable getDialogFromId(String id){
        return ChatDialogDbHelper.getInstance().getDialog(id);
    }

}
