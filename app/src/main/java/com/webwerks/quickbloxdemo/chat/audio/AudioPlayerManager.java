package com.webwerks.quickbloxdemo.chat.audio;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

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
 * Created by webwerks on 8/5/17.
 */

public class AudioPlayerManager implements MediaPlayer.OnCompletionListener{

    private static AudioPlayerManager instance;
    private MediaPlayer player;
    private static Timer timer;
    Observer<Integer[]> mObserver;
    private int currentFileId;

    public int getCurrentFileId() {
        return currentFileId;
    }

    public void setCurrentFileId(int currentFileId) {
        this.currentFileId = currentFileId;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mObserver.onComplete();
    }

    private class SeekTask extends TimerTask {
        public void run() {
            handler.sendEmptyMessage(0);
        }
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (player != null && isPlaying()) {

                 Observable.create(new ObservableOnSubscribe<Integer[]>() {
                            @Override
                            public void subscribe(ObservableEmitter<Integer[]> e) throws Exception {
                                Integer i[] = new Integer[2];
                                i[0] = player.getCurrentPosition();
                                i[1] = player.getDuration();

                                e.onNext(i);
                            }
                        }).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(mObserver);
            }
        }
    };

    public void seekToPosition(int position) {
        if (player != null) {
            player.seekTo(position);
            startPlayer();
        }
    }

    public static synchronized  AudioPlayerManager getInstance(){
        if(instance==null)
            instance=new AudioPlayerManager();

        return instance;
    }

    public boolean isPlaying(){
        if(player!=null){
            return player.isPlaying();
        }
        return false;
    }

    public void initializePlayer(String path, Observer<Integer[]> observer ){
        timer = new Timer();
        mObserver=observer;
        try {
            player = new MediaPlayer();
            player.setDataSource(path);
            player.setOnCompletionListener(this);
            player.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getDuration(){
        if(player!=null)
            return player.getDuration();
        return 0;
    }

    public void startPlayer(){
        if(player!=null){
            if(player.isPlaying()){
                player.seekTo(player.getCurrentPosition());
            }
            player.start();
            timer.scheduleAtFixedRate(new SeekTask(), 0, 1000);
        }
    }

    public void pausePlayer(){
        if(player!=null)
            player.pause();
    }

    public void stopPlayer(){
        if(player!=null){
            player.stop();
        }
    }

    public void release(){
        if(player!=null){
            stopPlayer();
            player.release();
            player=null;
        }
    }
}
