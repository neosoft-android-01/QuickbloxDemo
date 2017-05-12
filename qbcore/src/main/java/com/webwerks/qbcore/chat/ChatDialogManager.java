package com.webwerks.qbcore.chat;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.utils.DialogUtils;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestGetBuilder;
import com.quickblox.users.model.QBUser;
import com.webwerks.qbcore.database.ChatDialogDbHelper;
import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.qbcore.models.Messages;
import com.webwerks.qbcore.models.User;
import com.webwerks.qbcore.utils.NetworkUtils;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by webwerks on 18/4/17.
 */

public class ChatDialogManager {

    public static Observable<List<ChatDialog>> getRecentChatDialogs(final Context context) {
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
                        //from DB
                        /*ChatDialogDbHelper.getInstance().getAllDialogs().subscribe(new Consumer() {
                            @Override
                            public void accept(Object o) throws Exception {
                                List<ChatDialog> dialogList = new ArrayList<ChatDialog>();
                                dialogList= (List<ChatDialog>) o;
                            }
                        });*/
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

    public static Observable<ChatDialog> createGroupChatDialog(final String dialogName, final List<Integer> occupantsId) {
        return Observable.fromCallable(new Callable<ChatDialog>() {
            @Override
            public ChatDialog call() throws Exception {
                try {
                    QBChatDialog dialog  = QBRestChatService.createChatDialog(createGroupDialog(dialogName,occupantsId)).perform();
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

    public static Observable<ChatDialog> createPrivateChatDialog(final int participantId) {
        // final QBUser qbUser=User.toQBUser(user);

        return Observable.fromCallable(new Callable<ChatDialog>() {
            @Override
            public ChatDialog call() throws Exception {
                try {
                    QBChatDialog dialog  = QBRestChatService.createChatDialog(DialogUtils.buildPrivateDialog(participantId)).perform();;
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

    public static Observable<ChatDialog> createPrivateChatDialog(User user) {
        final QBUser qbUser=User.toQBUser(user);

        return Observable.fromCallable(new Callable<ChatDialog>() {
            @Override
            public ChatDialog call() throws Exception {
                try {
                    QBChatDialog dialog  = QBRestChatService.createChatDialog(DialogUtils.buildDialog(qbUser)).perform();;
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

    // for GROUP
    private static QBChatDialog createGroupDialog(String dialogName,List<Integer> occupantsId){
        return DialogUtils.buildDialog(dialogName, QBDialogType.GROUP,occupantsId);
    }

    public static Observable<List<Messages>> getDialogMessages(final ChatDialog dialog) {

        return Observable.fromCallable(new Callable<List<Messages>>() {
            @Override
            public List<Messages> call() throws Exception {
                QBRequestGetBuilder messageGetBuilder = new QBRequestGetBuilder();
                messageGetBuilder.setLimit(500);

                try {
                    QBChatDialog qbChatDialog=ChatDialog.toQbChatDialog(dialog);
                    ArrayList<QBChatMessage> chatMessages=QBRestChatService.getDialogMessages(qbChatDialog, messageGetBuilder).perform();

                    List<Messages> messages=new ArrayList<Messages>();
                    for(QBChatMessage chatMessage:chatMessages){
                        messages.add(Messages.getChatMessage(chatMessage));
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

    public static Observable<Boolean> joinGroup(final ChatDialog chatDialog){

        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                QBChatDialog qbChatDialog=ChatDialog.toQbChatDialog(chatDialog);
                DiscussionHistory history = new DiscussionHistory();
                history.setMaxStanzas(0);
                try {
                    qbChatDialog.join(history);
                } catch (XMPPException e) {
                    e.printStackTrace();
                    throw new Exception(e.getMessage());
                } catch (SmackException e) {
                    e.printStackTrace();
                    throw new Exception(e.getMessage());
                }

                return true;
            }
        });



    }

    public static Observable<Boolean>  leaveGroup(final ChatDialog chatDialog){

        return Observable.fromCallable(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                QBChatDialog qbChatDialog=ChatDialog.toQbChatDialog(chatDialog);
                try {
                    qbChatDialog.leave();
                } catch (XMPPException e) {
                    e.printStackTrace();
                    throw new Exception(e.getMessage());
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                    throw new Exception(e.getMessage());
                }
                return true;
            }
        });
    }

    public static Observable getDialogFromId(String id){
        return ChatDialogDbHelper.getInstance().getDialog(id);
    }

}
