package com.webwerks.quickbloxdemo.chat.audio;

import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;

import com.webwerks.quickbloxdemo.utils.DateUtils;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by webwerks on 9/5/17.
 */

public class AudioRecodeManager {

    private static AudioRecodeManager instance;
    private static File filePath;
    private static Timer timer;
    Observer<String> mObserver;
    private MediaRecorder mediaRecorder;
    private long startTime = 0L;
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Observable.create(new ObservableOnSubscribe<String>() {
                @Override
                public void subscribe(ObservableEmitter<String> e) throws Exception {
                    long timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                    final String time = DateUtils.getDurationFromMilliseconds(Integer.parseInt(timeInMilliseconds + ""));
                    e.onNext(time);
                }
            }).subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(mObserver);
        }
    };

    public void cancelTimer(){
        timer.cancel();
    }

    public static AudioRecodeManager getInstance() {
        if (instance == null)
            instance = new AudioRecodeManager();

        return instance;
    }

    private void initializeRecode(File file) {
        timer = new Timer();
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setOutputFile(file.getPath());
    }

    public void startRecode(File file, Observer<String> observer) {
        filePath = file;
        mObserver = observer;
        initializeRecode(file);
        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            startTime = SystemClock.uptimeMillis();
            timer.scheduleAtFixedRate(new AudioTimerTask(), 0, 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void stopRecode() {
        if (mediaRecorder != null) {
            try {
                timer.cancel();
                mediaRecorder.stop();
                mediaRecorder.reset();
                mediaRecorder.release();
                mediaRecorder=null;
            } catch (RuntimeException stopException) {
                //handle cleanup here
                filePath.delete();
            }
        }

    }

    private class AudioTimerTask extends TimerTask {
        public void run() {
            handler.sendEmptyMessage(0);
        }
    }

}
