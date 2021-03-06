package com.webwerks.qbcore.chat;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBRoster;
import com.quickblox.chat.QBSignaling;
import com.quickblox.chat.QBWebRTCSignaling;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBRosterListener;
import com.quickblox.chat.listeners.QBSubscriptionListener;
import com.quickblox.chat.listeners.QBVideoChatSignalingManagerListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacks;
import com.quickblox.videochat.webrtc.callbacks.QBRTCSessionConnectionCallbacks;
import com.quickblox.videochat.webrtc.exception.QBRTCException;
import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.qbcore.models.Messages;
import com.webwerks.qbcore.models.Presence;
import com.webwerks.qbcore.models.RealmInteger;
import com.webwerks.qbcore.models.User;
import com.webwerks.qbcore.utils.RealmHelper;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.RealmList;


/**
 * Created by webwerks on 17/4/17.
 */

public class ChatManager implements QBRTCClientSessionCallbacks ,QBRTCSessionConnectionCallbacks{

    public static String TAG="ChatManager";

    public static ChatManager instance;
    private static IncomingMessageListener messageReceivedListener;
    private  ChatMessageListener chatMessageListener;
    private static CurrentCallStateCallback currentCallStateCallback;

    private QBChatService chatService;
    private static QBRTCSession currentSession;

    private QBSubscriptionListener subscriptionListener;
    private QBRosterListener rosterListener;
    private static QBRoster сhatRoster ;

    public static ChatManager getInstance(){
        if(instance==null) {
            instance = new ChatManager();
        }
        return instance;
    }

    public ChatManager(){
        initChatService();
    }

    public static QBRTCSession getCurrentSession() {
        return currentSession;
    }

    private static void setCurrentSession(QBRTCSession qbCurrentSession) {
        currentSession = qbCurrentSession;
    }

    public static void acceptIncomingCall(){
        if(currentSession!=null)
            currentSession.acceptCall(null);
    }

    public static void rejectIncomingCall(){
        if(currentSession!=null)
            currentSession.rejectCall(null);
    }

    public static void hangUpCall(){
        if(currentSession!=null)
            currentSession.hangUp(null);
    }

    public static void setCurrentCallStateCallback(CurrentCallStateCallback listener){
        currentCallStateCallback=listener;
    }

    @Override
    public void onReceiveNewSession(QBRTCSession qbrtcSession) {
        Log.e("TAG","NEW REQUEST" + qbrtcSession.getCallerID() + ":::" + qbrtcSession.getUserInfo());
        setCurrentSession(qbrtcSession);
        qbrtcSession.addSessionCallbacksListener(this);
        messageReceivedListener.onIncomingCall();
    }

    @Override
    public void onUserNoActions(QBRTCSession qbrtcSession, Integer integer) {
        Log.e("TAG","NO ACTION" + qbrtcSession.getCallerID());
    }

    @Override
    public void onSessionStartClose(QBRTCSession qbrtcSession) {
        Log.e("TAG","onSessionStartClose" + qbrtcSession.getCallerID());
        if (qbrtcSession.equals(getCurrentSession())) {
            currentCallStateCallback.onCallStopped();
        }
    }

    // Caller not received call
    @Override
    public void onUserNotAnswer(QBRTCSession qbrtcSession, Integer integer) {
        Log.e("TAG","NOT ANSWERING in time" + qbrtcSession.getCallerID());
        ChatManager.hangUpCall();
    }

    // Callee hung up call
    @Override
    public void onCallRejectByUser(QBRTCSession qbrtcSession, Integer userID, Map<String, String> map) {
        Log.e("TAG","REJECTED" + qbrtcSession.getCallerID());
        if (qbrtcSession.equals(getCurrentSession())) {
            ChatManager.hangUpCall();
        }
    }

    @Override
    public void onCallAcceptByUser(QBRTCSession qbrtcSession, Integer integer, Map<String, String> map) {
        Log.e("TAG","ACCEPTED" + qbrtcSession.getCallerID());
        setCurrentSession(qbrtcSession);
        qbrtcSession.addSessionCallbacksListener(this);
    }

    // Caller hung up the call
    @Override
    public void onReceiveHangUpFromUser(QBRTCSession qbrtcSession, Integer userID, Map<String, String> map) {
        Log.e("TAG","onReceiveHangUpFromUser" +"::"+qbrtcSession.getCallerID() + ":::" + userID);
        if (qbrtcSession.equals(getCurrentSession())) {
            if (userID.equals(qbrtcSession.getCallerID())) {
                ChatManager.hangUpCall();
            }
        }
    }

    @Override
    public void onSessionClosed(QBRTCSession qbrtcSession) {
        Log.e("TAG","onSessionClosed" + qbrtcSession.getCallerID() + ":::" + qbrtcSession.equals(getCurrentSession()));
        if (qbrtcSession.equals(getCurrentSession())) {
            releaseCurrentSession();
            currentCallStateCallback.onCallConnectionClose(qbrtcSession.getCallerID());
        }
     }

    @Override
    public void onConnectedToUser(QBRTCSession qbrtcSession, Integer integer) {
        Log.e("TAG","onConnectedToUser" + qbrtcSession.getCallerID());
        currentCallStateCallback.onCallStarted();
    }

    @Override
    public void onDisconnectedFromUser(QBRTCSession qbrtcSession, Integer integer) {
        Log.e("TAG","onDisconnectedFromUser" + qbrtcSession.getCallerID());
    }

    @Override
    public void onConnectionClosedForUser(QBRTCSession qbrtcSession, Integer integer) {
        Log.e("TAG","onConnectionClosedForUser" + qbrtcSession.getCallerID());
        releaseCurrentSession();
        currentCallStateCallback.onCallConnectionClose(qbrtcSession.getCallerID());
    }

    @Override
    public void onStartConnectToUser(QBRTCSession qbrtcSession, Integer integer) {
    }

    @Override
    public void onDisconnectedTimeoutFromUser(QBRTCSession qbrtcSession, Integer integer) {
    }

    @Override
    public void onConnectionFailedWithUser(QBRTCSession qbrtcSession, Integer integer) {
        Log.e(TAG,qbrtcSession.getState()+"::::");
    }

    @Override
    public void onError(QBRTCSession qbrtcSession, QBRTCException e) {
        Log.e(TAG,e.getMessage());
    }

    // Receive incoming message
    private class ChatMessageListener implements QBChatDialogMessageListener {
        @Override
        public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
            if(!QBChatService.getInstance().getUser().getId().equals(integer)) {
                messageReceivedListener.onMessageReceived(Messages.getChatMessage(qbChatMessage));
            }
        }

        @Override
        public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
        }
    }

    public static void setAudioEnabled(boolean isAudioEnabled) {
        if (currentSession != null && currentSession.getMediaStreamManager() != null) {
            currentSession.getMediaStreamManager().getLocalAudioTrack().setEnabled(isAudioEnabled);
        }
    }

    private void setVideoEnabled(boolean isVideoEnabled) {
        if (currentSession != null && currentSession.getMediaStreamManager() != null) {
            currentSession.getMediaStreamManager().getLocalVideoTrack().setEnabled(isVideoEnabled);
        }
    }

    public void initSession(final Context context, ChatDialog dialog, IncomingMessageListener listener){
        QBChatDialog chatDialog=ChatDialog.toQbChatDialog(dialog);
        chatMessageListener=new ChatMessageListener();
        chatDialog.addMessageListener(chatMessageListener);
        chatDialog.initForChat(chatService);
        messageReceivedListener=listener;

        // Audio calling
        chatService.getVideoChatWebRTCSignalingManager().addSignalingManagerListener(new QBVideoChatSignalingManagerListener() {
            @Override
            public void signalingCreated(QBSignaling qbSignaling, boolean createdLocally) {
                if(!createdLocally){
                    QBRTCClient.getInstance(context).addSignaling((QBWebRTCSignaling) qbSignaling);
                }
            }
        });
        QBRTCClient.getInstance(context).prepareToProcessCalls();
        QBRTCClient.getInstance(context).addSessionCallbacksListener(this);
    }

    public void stopSession(Context context,ChatDialog dialog){
        QBChatDialog chatDialog=ChatDialog.toQbChatDialog(dialog);
        chatDialog.removeMessageListrener(chatMessageListener);
        QBRTCClient.getInstance(context).removeSessionsCallbacksListener(this);
        QBRTCClient.getInstance(context).destroy();
    }

    public void releaseCurrentSession() {
        Log.d(TAG, "Release current session");
        if (currentSession != null) {
            this.currentSession.removeSessionCallbacksListener(this);
           /* this.currentSession.removeSignalingCallback(this);
            rtcClient.removeSessionsCallbacksListener(CallActivity.this);*/
            this.currentSession = null;
        }
    }

    private void initChatService(){
        QBChatService.ConfigurationBuilder configurationBuilder=new QBChatService.ConfigurationBuilder();
        configurationBuilder.setKeepAlive(true).setSocketTimeout(0);
        configurationBuilder.setUseTls(true);
        configurationBuilder.setAutojoinEnabled(true);

        QBChatService.setConfigurationBuilder(configurationBuilder);
        QBChatService.setDebugEnabled(true);
        QBChatService.setDefaultPacketReplyTimeout(10000);

        chatService = QBChatService.getInstance();
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
                                initRoster();
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

    public static void startCall(Context context, RealmList<RealmInteger> occupant) {
        Log.e(TAG, "startCall()");
        List<Integer> opponentsList = RealmHelper.fromIntegerRelamList(occupant);

        for(Integer user:opponentsList){
            Log.e(TAG,user+":::"+checkUserPresence(user));
        }

        QBRTCTypes.QBConferenceType conferenceType = QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;
        QBRTCClient qbrtcClient = QBRTCClient.getInstance(context);
        QBRTCSession newQbRtcSession = qbrtcClient.createNewSessionWithOpponents(opponentsList, conferenceType);
        setCurrentSession(newQbRtcSession);
        newQbRtcSession.startCall(null);
    }

    public static Presence checkUserPresence(int userId){
        QBPresence presence = сhatRoster.getPresence(userId);
        if (presence == null) {
            return Presence.OFFLINE;
        }

        if (presence.getType() == QBPresence.Type.online)
            return Presence.ONLINE;
        else
            return Presence.OFFLINE;
    }

    private void initRoster() {
        initSubscriptionListener();
        сhatRoster = chatService.getRoster(QBRoster.SubscriptionMode.mutual, subscriptionListener);
        сhatRoster.addRosterListener(new QBRosterListener() {
            @Override
            public void entriesDeleted( Collection< Integer > userIds ) {
                Log.e("entriesDeleted: ",""+ userIds);
            }

            @Override
            public void entriesAdded( Collection< Integer > userIds ) {
                Log.e("entriesAdded: ",""+ userIds);
            }

            @Override
            public void entriesUpdated( Collection< Integer > userIds ) {
                Log.e("entriesUpdated: ",""+ userIds);
            }

            @Override
            public void presenceChanged( QBPresence presence ) {

                if(presence.getType() == QBPresence.Type.online ){
                }
            }
        });
    }

    private void initSubscriptionListener() {
        subscriptionListener = new QBSubscriptionListener() {
            @Override
            public void subscriptionRequested( int userId ) {
                confirmAddRequest(userId);
            }
        };
    }

    public void confirmAddRequest(int userID) {
        if ( сhatRoster == null ) {
            Log.e( "сhatRoster == null ", "Please login first" );
            return;
        }

        try {
            сhatRoster.confirmSubscription(userID);
        } catch (SmackException.NotConnectedException | SmackException.NotLoggedInException | SmackException.NoResponseException e) {
            Log.e("error: ", e.getClass().getSimpleName());
        } catch (XMPPException e) {
            Log.e("error: ", e.getLocalizedMessage());
        }
    }

}
