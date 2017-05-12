package com.webwerks.qbcore.chat;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.users.model.QBUser;
import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.qbcore.models.Messages;
import com.webwerks.qbcore.models.User;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by webwerks on 17/4/17.
 */

public class ChatManager {

    public static String TAG="ChatManager";

    public static ChatManager instance;
    private IncomingMessageListener messageReceivedListener;
    private ChatMessageListener chatMessageListener;

    private QBChatService chatService;

    public static ChatManager getInstance(){
        if(instance==null) {
            instance = new ChatManager();
        }
        return instance;
    }

    public ChatManager(){
        initChatService();
    }

    private class ChatMessageListener implements QBChatDialogMessageListener {
        @Override
        public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
            messageReceivedListener.onMessageReceived(Messages.getChatMessage(qbChatMessage));
        }

        @Override
        public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
        }
    }

    public void initSession(ChatDialog dialog,IncomingMessageListener listener){
        QBChatDialog chatDialog=ChatDialog.toQbChatDialog(dialog);


        if(chatDialog.getType().equals(QBDialogType.GROUP)){
            ChatDialogManager.joinGroup(dialog).subscribe(new Consumer<Boolean>() {
                @Override
                public void accept(Boolean aBoolean) throws Exception {

                }
            }, new Consumer<Throwable>() {
                @Override
                public void accept(Throwable throwable) throws Exception {

                }
            });
        }

        chatMessageListener=new ChatMessageListener();
        chatDialog.addMessageListener(chatMessageListener);
        chatDialog.initForChat(chatService);

        messageReceivedListener=listener;
    }

    public void stopSession(ChatDialog dialog){
        QBChatDialog chatDialog=ChatDialog.toQbChatDialog(dialog);
        chatDialog.removeMessageListrener(chatMessageListener);
    }

    private void initChatService(){
        QBChatService.ConfigurationBuilder configurationBuilder=new QBChatService.ConfigurationBuilder();
        configurationBuilder.setKeepAlive(true).setSocketTimeout(0);
        configurationBuilder.setUseTls(true);
        QBChatService.setConfigurationBuilder(configurationBuilder);
        QBChatService.setDebugEnabled(true);
        QBChatService.setDefaultPacketReplyTimeout(10000);

        chatService = QBChatService.getInstance();
        //chatService.addConnectionListener(chatConnectionListener);

        // stream management
        chatService.setUseStreamManagement(true);
    }

    public boolean isLogged() {
        return QBChatService.getInstance().isLoggedIn();
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

    /*private ConnectionListener chatConnectionListener=new ConnectionListener() {
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
    };*/

}
