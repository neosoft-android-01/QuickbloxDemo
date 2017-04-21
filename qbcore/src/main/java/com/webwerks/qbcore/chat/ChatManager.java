package com.webwerks.qbcore.chat;

import android.util.Log;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBChatDialogMessageSentListener;
import com.quickblox.chat.listeners.QBChatDialogTypingListener;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.users.model.QBUser;
import com.webwerks.qbcore.models.User;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by webwerks on 17/4/17.
 */

public class ChatManager {

    public static String TAG="ChatManager";
    public static ChatManager instance;

    private QBChatService chatService;
    private QBChatDialogMessageListener privateChatMsgListener;
    private QBChatDialogTypingListener privateChatTypingListener;
    private QBChatDialogMessageSentListener privateChatMsgSentListener;

    public static ChatManager getInstance(){
        if(instance==null) {
            instance = new ChatManager();
        }
        return instance;
    }

    public ChatManager(){
        initChatService();
    }

    private void initChatService(){
        QBChatService.ConfigurationBuilder configurationBuilder=new QBChatService.ConfigurationBuilder();
        configurationBuilder.setKeepAlive(true).setSocketTimeout(0);
        QBChatService.setConfigurationBuilder(configurationBuilder);
        QBChatService.setDebugEnabled(true);
        QBChatService.setDefaultPacketReplyTimeout(10000);

        chatService = QBChatService.getInstance();
        chatService.addConnectionListener(chatConnectionListener);

        // stream management
        chatService.setUseStreamManagement(true);
    }

    public boolean isLogged() {
        return QBChatService.getInstance().isLoggedIn();
    }

    public static Observable getRecentChatList(){
       return ChatDialogManager.getRecentChatDialogs();
    }

    public Single<User> loginToChat(User user){

        QBUser qbUser=User.toQBUser(user);
        return Single.just(qbUser)
                .map(new Function<QBUser, User>() {
                    @Override
                    public User apply(QBUser user) throws Exception {

                        if(chatService.isLoggedIn()){
                            return User.fromQbUser(user);
                        }else{
                            try {
                                chatService.login(user);
                                return User.fromQbUser(user);
                            }catch (Exception e){
                                e.printStackTrace();
                                throw new Exception(e.getMessage());
                            }
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void initMessageSentListener() {
        privateChatMsgSentListener = new QBChatDialogMessageSentListener() {
            @Override
            public void processMessageSent(String dialogId, QBChatMessage qbChatMessage) {
                Log.d(TAG,"message " + qbChatMessage.getId() + " sent to dialog " + dialogId);
            }

            @Override
            public void processMessageFailed(String dialogId, QBChatMessage qbChatMessage) {
                Log.d(TAG,"send message " + qbChatMessage.getId() + " has failed to dialog " + dialogId);
            }
        };

    }

    private void initPrivateChatListener(){
        privateChatMsgListener=new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
                Log.d(TAG,"received message: " + qbChatMessage.getId());

                if (qbChatMessage.getSenderId().equals(chatService.getUser().getId())) {
                    Log.d(TAG,"Message comes here from carbons");
                }
            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
                Log.d(TAG,"processError: " + e.getLocalizedMessage());
            }
        };
    }

    private void initIsTypingListener() {
        // Create 'is typing' listener
        privateChatTypingListener = new QBChatDialogTypingListener() {
            @Override
            public void processUserIsTyping(String dialogId, Integer senderId) {
                Log.d(TAG,"user " + senderId + " is typing. Private dialog id: " + dialogId);
            }

            @Override
            public void processUserStopTyping(String dialogId, Integer senderId) {
                Log.d(TAG,"user " + senderId + " stop typing. Private dialog id: " + dialogId);
            }
        };
    }

    private ConnectionListener chatConnectionListener=new ConnectionListener() {
        @Override
        public void connected(XMPPConnection xmppConnection) {
            Log.d(TAG,"CONNECTED");
        }

        @Override
        public void authenticated(XMPPConnection xmppConnection, boolean b) {
            Log.d(TAG,"AUTHENTICATED");
        }

        @Override
        public void connectionClosed() {
            Log.d(TAG,"CONNECTION CLOSED");
        }

        @Override
        public void connectionClosedOnError(Exception e) {
            Log.d(TAG,"CONNECTION CLOSED ERROR " + e.getLocalizedMessage());
        }

        @Override
        public void reconnectionSuccessful() {
            Log.d(TAG,"RECONNECTION SUCCESSFUL");
        }

        @Override
        public void reconnectingIn(int i) {
            if (i % 5 == 0) {
                Log.e(TAG,"reconnectingIn: " + i);
            }
        }

        @Override
        public void reconnectionFailed(Exception e) {
            Log.d(TAG,"RECONNECTION ERROR " + e.getLocalizedMessage());
        }
    };

}
