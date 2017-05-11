package com.webwerks.quickbloxdemo.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.webwerks.qbcore.models.MessageType;
import com.webwerks.qbcore.models.Messages;
import com.webwerks.qbcore.utils.Constant;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.chat.audio.AudioPlayerManager;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.utils.DateUtils;
import com.webwerks.quickbloxdemo.utils.FileUtil;

import java.io.File;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

/**
 * Created by webwerks on 27/4/17.
 */

public class ChatLayoutDataBindHelper {

    public static void configureTextChatHolder(Context context, ChatAdapter.TextChatHolder holder, Messages message){
        boolean sent=true;
        if(message.getSenderId()!=null) {
            if (message.getSenderId() == App.getAppInstance().getCurrentUser().id)
                sent = true;
            else
                sent = false;
        }

        holder.lblText.setText(message.getMsg());
        holder.lblTime.setText(DateUtils.getTimeText(message.getDateSent(),"chat"));
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin=10;

        RelativeLayout.LayoutParams timeParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        if(sent){
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            timeParams.addRule(RelativeLayout.LEFT_OF, R.id.lblText);
            timeParams.rightMargin=30;
            holder.lblText.setBackgroundResource(R.drawable.sent_msg_bg);
            holder.lblText.setTextColor(ContextCompat.getColor(context,android.R.color.white));
        }else{
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            timeParams.addRule(RelativeLayout.RIGHT_OF,R.id.lblText);
            timeParams.leftMargin=30;
            holder.lblText.setBackgroundResource(R.drawable.received_msg_bg);
            holder.lblText.setTextColor(Color.parseColor("#6438B0"));
        }
        holder.lblText.setLayoutParams(params);
        holder.lblTime.setLayoutParams(timeParams);
    }

    public static void configureImageAttachment(final Context context,final ChatAdapter.ImageAttachmentHolder holder,final Messages message){
        boolean sent=true;
        if(message.getSenderId()!=null) {
            if (message.getSenderId() == App.getAppInstance().getCurrentUser().id)
                sent = true;
            else
                sent = false;
        }

        FileUtil.getAttachmentPath(Integer.parseInt(message.getAttachments().get(0).getId()), MessageType.IMAGE)
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        Glide.with(context)
                                .load(s)
                                .error(R.drawable.ic_placeholder)
                                .into(holder.imgAttachment);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Glide.with(context)
                                .load(R.drawable.ic_placeholder)
                                .error(R.drawable.ic_placeholder)
                                .placeholder(R.drawable.ic_placeholder)
                                .into(holder.imgAttachment);
                    }
                });

        holder.lblTime.setText(DateUtils.getTimeText(message.getDateSent(),"chat"));

        DisplayMetrics displayMetrics=App.getAppInstance().getDisplayMatrix();
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(displayMetrics.widthPixels/3, displayMetrics.widthPixels/3);
        params.topMargin=10;

        RelativeLayout.LayoutParams timeParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        if(sent){
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            timeParams.addRule(RelativeLayout.LEFT_OF, R.id.rl_image);
            timeParams.rightMargin=30;
        }else{
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            timeParams.addRule(RelativeLayout.RIGHT_OF,R.id.rl_image);
            timeParams.leftMargin=30;
        }
        holder.rlImageView.setLayoutParams(params);
        holder.lblTime.setLayoutParams(timeParams);
    }

    public static void configureLocation(final Context context, final ChatAdapter.LocationAttachmentHolder holder, final Messages message){
        boolean sent=true;
        if(message.getSenderId()!=null) {
            if (message.getSenderId() == App.getAppInstance().getCurrentUser().id)
                sent = true;
            else
                sent = false;
        }

        if(!TextUtils.isEmpty(message.getLocationAttachment().getUrl())){
            Glide.with(context)
                    .load(message.getLocationAttachment().getUrl())
                    .error(R.drawable.ic_placeholder)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(holder.imgLocationAttachment);
        }else{
            Glide.with(context)
                    .load(R.drawable.ic_placeholder)
                    .error(R.drawable.ic_placeholder)
                    .placeholder(R.drawable.ic_placeholder)
                    .into(holder.imgLocationAttachment);
        }

        DisplayMetrics displayMetrics=App.getAppInstance().getDisplayMatrix();
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(displayMetrics.widthPixels/2, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin=10;

        RelativeLayout.LayoutParams timeParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timeParams.addRule(RelativeLayout.CENTER_VERTICAL);

        if(sent){
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            timeParams.addRule(RelativeLayout.LEFT_OF, R.id.rl_location_message);
            timeParams.rightMargin=30;

            holder.rlRoot.setBackgroundResource(R.drawable.sent_msg_bg);
            holder.lblLocationName.setTextColor(ContextCompat.getColor(context,android.R.color.white));
            holder.lblLocationDesc.setTextColor(ContextCompat.getColor(context,android.R.color.white));
        }else{
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            timeParams.addRule(RelativeLayout.RIGHT_OF,R.id.rl_location_message);
            timeParams.leftMargin=30;

            holder.rlRoot.setBackgroundResource(R.drawable.received_msg_bg);
            holder.lblLocationName.setTextColor(Color.parseColor("#6438B0"));
            holder.lblLocationDesc.setTextColor(Color.parseColor("#6438B0"));
        }

        holder.rlRoot.setLayoutParams(params);
        holder.lblTime.setLayoutParams(timeParams);

        holder.lblTime.setText(DateUtils.getTimeText(message.getDateSent(),"chat"));
        holder.lblLocationName.setText(message.getLocationAttachment().getLocationName());
        holder.lblLocationDesc.setText(message.getLocationAttachment().getLocationDesc());

        holder.rlRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = message.getLocationAttachment().getLatitude()+","
                        +message.getLocationAttachment().getLongitude()+ "(" + message.getLocationAttachment().getLocationName() + ")";
                String encodedQuery = Uri.encode(query);
                String uriString = "geo:" + message.getLocationAttachment().getLatitude()+","+
                        message.getLocationAttachment().getLongitude()+ "?q=" + encodedQuery + "&z=16";
                Uri intentUri = Uri.parse( uriString);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(mapIntent);
                }
            }
        });
    }

    public static void configureAudioAttachment(final ChatAdapter chatAdapter, final Context context, final ChatAdapter.AudioAttachmentHolder holder, final Messages message){
        boolean sent=true;
        if(message.getSenderId()!=null) {
            if (message.getSenderId() == App.getAppInstance().getCurrentUser().id)
                sent = true;
            else
                sent = false;
        }

        DisplayMetrics displayMetrics=App.getAppInstance().getDisplayMatrix();
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams((int)(displayMetrics.widthPixels*(0.80)), ViewGroup.LayoutParams.WRAP_CONTENT);
        params.topMargin=10;

        RelativeLayout.LayoutParams timeParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timeParams.addRule(RelativeLayout.CENTER_VERTICAL);

        if(sent){
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            holder.llRoot.setBackgroundResource(R.drawable.sent_msg_bg);

            timeParams.addRule(RelativeLayout.LEFT_OF, R.id.ll_root);
            timeParams.rightMargin=30;
            holder.lblCurrent.setTextColor(ContextCompat.getColor(context,android.R.color.white));
            holder.lblTotalDuration.setTextColor(ContextCompat.getColor(context,android.R.color.white));
        }else{
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            holder.llRoot.setBackgroundResource(R.drawable.received_msg_bg);

            timeParams.addRule(RelativeLayout.RIGHT_OF,R.id.ll_root);
            timeParams.leftMargin=30;
            holder.lblCurrent.setTextColor(Color.parseColor("#6438B0"));
            holder.lblTotalDuration.setTextColor(Color.parseColor("#6438B0"));
        }

        holder.llRoot.setLayoutParams(params);
        holder.lblTime.setLayoutParams(timeParams);
        holder.lblTime.setText(DateUtils.getTimeText(message.getDateSent(),"chat"));

        final Observer seekUpdate=new Observer<Integer[]>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(Integer[] value) {
                message.setCurrentDuration(value[0]);
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable e) {
            }

            @Override
            public void onComplete() {
                message.setCurrentDuration(0);
                chatAdapter.notifyDataSetChanged();
            }
        };

        holder.lblTotalDuration.setText(message.getTotalDuration()==0?"":DateUtils.getDurationFromMilliseconds(message.getTotalDuration()));
        holder.lblCurrent.setText(message.getTotalDuration()==0?"":DateUtils.getDurationFromMilliseconds(message.getCurrentDuration())+" / ");
        holder.audioProgress.setMax(message.getTotalDuration());
        holder.audioProgress.setProgress(message.getCurrentDuration());

        final File file=FileUtil.getAudioFile(Integer.parseInt(message.getAttachments().get(0).getId()));
        if(file.exists()){
            if(AudioPlayerManager.getInstance().isPlaying() &&
                    (Integer.parseInt(message.getAttachments().get(0).getId())==AudioPlayerManager.getInstance().getCurrentFileId())) {
                holder.btnDownload.setImageResource(R.drawable.ic_pause);
                holder.btnDownload.setTag(Constant.AUDIO_STATE_PAUSE);
            } else {
                holder.btnDownload.setImageResource(R.drawable.ic_play);
                holder.btnDownload.setTag(Constant.AUDIO_STATE_PLAY);
                holder.audioProgress.setProgress(0);
                holder.lblTotalDuration.setText("");
                holder.lblCurrent.setText("");
            }
        }else{
            holder.btnDownload.setImageResource(R.drawable.ic_download);
            holder.btnDownload.setTag(Constant.AUDIO_STATE_DOWNLOAD);
        }

        holder.btnDownload.setTag(R.id.audio_data,message);
        holder.btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                final Messages messages= (Messages) view.getTag(R.id.audio_data);
                final int fileId=Integer.parseInt(messages.getAttachments().get(0).getId());
                switch ((String)view.getTag()/*messages.getState()*/){
                    case Constant.AUDIO_STATE_DOWNLOAD:
                        FileUtil.getAttachmentPath(fileId, MessageType.AUDIO)
                                .subscribe(new Consumer<String>() {
                                    @Override
                                    public void accept(String s) throws Exception {
                                        ((ImageButton)view).setImageResource(R.drawable.ic_play);
                                        ((ImageButton)view).setTag(Constant.AUDIO_STATE_PLAY);

                                        AudioPlayerManager.getInstance().initializePlayer(s,seekUpdate);
                                        AudioPlayerManager.getInstance().setCurrentFileId(fileId);
                                        chatAdapter.notifyDataSetChanged();
                                        messages.setTotalDuration(AudioPlayerManager.getInstance().getDuration());
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                    }
                                });
                        break;

                    case Constant.AUDIO_STATE_PLAY:
                        ((ImageButton)view).setImageResource(R.drawable.ic_pause);
                        ((ImageButton)view).setTag(Constant.AUDIO_STATE_PAUSE);

                        if(AudioPlayerManager.getInstance().getCurrentFileId()!=fileId) {

                            AudioPlayerManager.getInstance().release();
                            FileUtil.getAttachmentPath(Integer.parseInt(((Messages) holder.btnDownload.getTag(R.id.audio_data)).getAttachments().get(0).getId()), MessageType.AUDIO)
                                    .subscribe(new Consumer<String>() {
                                        @Override
                                        public void accept(String s) throws Exception {
                                            AudioPlayerManager.getInstance().initializePlayer(s, seekUpdate);
                                            AudioPlayerManager.getInstance().setCurrentFileId(fileId);
                                            messages.setTotalDuration(AudioPlayerManager.getInstance().getDuration());
                                            AudioPlayerManager.getInstance().startPlayer();
                                            chatAdapter.notifyDataSetChanged();
                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                        }
                                    });
                        }else{
                            AudioPlayerManager.getInstance().startPlayer();
                        }
                        break;

                    case Constant.AUDIO_STATE_PAUSE:
                        ((ImageButton)view).setImageResource(R.drawable.ic_play);
                        ((ImageButton)view).setTag(Constant.AUDIO_STATE_PLAY);
                        AudioPlayerManager.getInstance().pausePlayer();
                        break;
                }
            }
        });

        holder.audioProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b)
                    AudioPlayerManager.getInstance().seekToPosition(i);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }
}