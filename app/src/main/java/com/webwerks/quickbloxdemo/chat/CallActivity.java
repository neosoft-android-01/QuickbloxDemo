package com.webwerks.quickbloxdemo.chat;

import android.databinding.ViewDataBinding;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.videochat.webrtc.AppRTCAudioManager;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.webwerks.qbcore.chat.ChatManager;
import com.webwerks.qbcore.chat.CurrentCallStateCallback;
import com.webwerks.qbcore.models.User;
import com.webwerks.qbcore.user.QbUserAuth;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.global.Constants;
import com.webwerks.quickbloxdemo.ui.activities.BaseActivity;

import java.util.List;

import io.reactivex.functions.Consumer;

/**
 * Created by webwerks on 29/5/17.
 */

public class CallActivity extends BaseActivity implements CurrentCallStateCallback{

    TextView lblRinging;
    Chronometer timerChronometer;

    boolean isIncoming=false;
    boolean isStarted;
    private AppRTCAudioManager audioManager;
    private QBRTCSession currentSession;

    private Runnable showIncomingCallWindowTask;
    private Handler showIncomingCallWindowTaskHandler;
    private boolean previousDeviceEarPiece;

    @Override
    public int getContentLayout() {
        return R.layout.activity_call;
    }

    @Override
    public void initializeUiComponents(ViewDataBinding binding) {
        isIncoming=getIntent().getBooleanExtra(Constants.EXTRA_IS_INCOMING_CALL,false);
        currentSession=ChatManager.getCurrentSession();

        lblRinging= (TextView) findViewById(R.id.lbl_ringing);
        timerChronometer= (Chronometer) findViewById(R.id.chronometer_timer_audio_call);

        ChatManager.setCurrentCallStateCallback(this);

        int userId=0;
        if(isIncoming){
            userId=currentSession.getCallerID();
            ChatManager.acceptIncomingCall();
            initIncomingCallTask();
        }else{
            List<Integer> userList=currentSession.getOpponents();
            for(Integer id:userList){
                if(id!=currentSession.getCallerID()){
                    userId=id;
                    break;
                }
            }
        }

        if(isIncoming){
           lblRinging.setVisibility(View.GONE);
        }

        QbUserAuth.getUserFromId(userId).subscribe(new Consumer<User>() {
            @Override
            public void accept(User user) throws Exception {
                ((TextView)findViewById(R.id.lbl_outgoing_opponents_names)).setText(user.fullName);
            }
        });

        initAudioManager();
        findViewById(R.id.btn_hang_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatManager.hangUpCall();
            }
        });
    }

    private void startTimer() {
        if (!isStarted) {
            timerChronometer.setVisibility(View.VISIBLE);
            timerChronometer.setBase(SystemClock.elapsedRealtime());
            timerChronometer.start();
            isStarted = true;
        }
    }

    private void stopTimer() {
        if (timerChronometer != null) {
            timerChronometer.stop();
            isStarted = false;
        }
    }


    private void initAudioManager() {
        audioManager = AppRTCAudioManager.create(this, new AppRTCAudioManager.OnAudioManagerStateListener() {
            @Override
            public void onAudioChangedState(AppRTCAudioManager.AudioDevice audioDevice) {

                if (audioManager.getSelectedAudioDevice() == AppRTCAudioManager.AudioDevice.EARPIECE) {
                    previousDeviceEarPiece = true;
                } else if (audioManager.getSelectedAudioDevice() == AppRTCAudioManager.AudioDevice.SPEAKER_PHONE) {
                    previousDeviceEarPiece = false;
                }
            }
        });

        audioManager.setDefaultAudioDevice(AppRTCAudioManager.AudioDevice.EARPIECE);
        //previousDeviceEarPiece = true;
        Log.e("CALL", "AppRTCAudioManager.AudioDevice.EARPIECE");

        audioManager.setOnWiredHeadsetStateListener(new AppRTCAudioManager.OnWiredHeadsetStateListener() {
            @Override
            public void onWiredHeadsetStateChanged(boolean plugged, boolean hasMicrophone) {
                //headsetPlugged = plugged;

                Toast.makeText(CallActivity.this,"Headset " + (plugged ? "plugged" : "unplugged"),Toast.LENGTH_SHORT).show();

                //if (onChangeDynamicCallback != null) {
                    if (!plugged) {
                        //showToastAfterHeadsetPlugged = false;
                        if (previousDeviceEarPiece) {
                            setAudioDeviceDelayed(AppRTCAudioManager.AudioDevice.EARPIECE);
                        } else {
                            setAudioDeviceDelayed(AppRTCAudioManager.AudioDevice.SPEAKER_PHONE);
                        }
                    }
                   /* onChangeDynamicCallback.enableDynamicToggle(plugged, previousDeviceEarPiece);
                }*/
            }
        });
        audioManager.init();
    }

    private void setAudioDeviceDelayed(final AppRTCAudioManager.AudioDevice audioDevice) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //showToastAfterHeadsetPlugged = true;
                audioManager.setAudioDevice(audioDevice);
            }
        }, 500);
    }

    private void setAudioEnabled(boolean isAudioEnabled) {
        if (currentSession != null && currentSession.getMediaStreamManager() != null) {
            currentSession.getMediaStreamManager().getLocalAudioTrack().setEnabled(isAudioEnabled);
        }
    }

    private void setVideoEnabled(boolean isVideoEnabled) {
        if (currentSession != null && currentSession.getMediaStreamManager() != null) {
            currentSession.getMediaStreamManager().getLocalVideoTrack().setEnabled(isVideoEnabled);
        }
    }

    private void startIncomeCallTimer(long time) {
        showIncomingCallWindowTaskHandler.postAtTime(showIncomingCallWindowTask, SystemClock.uptimeMillis() + time);
    }

    private void stopIncomeCallTimer() {
        Log.d("CALL", "stopIncomeCallTimer");
        showIncomingCallWindowTaskHandler.removeCallbacks(showIncomingCallWindowTask);
    }

    private void initIncomingCallTask() {
        showIncomingCallWindowTaskHandler = new Handler(Looper.myLooper());
        showIncomingCallWindowTask = new Runnable() {
            @Override
            public void run() {
                if (currentSession == null) {
                    return;
                }

                QBRTCSession.QBRTCSessionState currentSessionState = currentSession.getState();
                if (QBRTCSession.QBRTCSessionState.QB_RTC_SESSION_NEW.equals(currentSessionState)) {
                        ChatManager.rejectIncomingCall();
                } else {
                   // hangUpCurrentSession();
                }
            }
        };
    }

    @Override
    public void onCallStarted() {
        lblRinging.setVisibility(View.INVISIBLE);
        startTimer();
    }

    @Override
    public void onCallStopped() {
        if (currentSession == null) {
            Log.e("CALL END", "currentSession = null onCallStopped");
            return;
        }
        stopTimer();
    }

    @Override
    public void onCallConnectionClose() {
        finish();
    }
}
