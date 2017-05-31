package com.webwerks.quickbloxdemo.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.webwerks.qbcore.models.Messages;
import com.webwerks.quickbloxdemo.R;

import java.util.List;

/**
 * Created by webwerks on 24/4/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TEXT = 0, PHOTO = 1, LOCATION = 2,AUDIO=3,CALL=4;
    List<Messages> messages;
    Context mContext;

    public ChatAdapter(Context context, List<Messages> messagesList) {
        mContext = context;
        messages = messagesList;
    }

    public void add(Messages msg) {
        messages.add(msg);
        notifyDataSetChanged();
    }

    /*public void update(Messages oldMessage){
      //  int i=messages.indexOf(oldMessage);
        //Log.e("CHAT ADAPTER",i +"::"+messages.contains(oldMessage));

      //  messages.remove(i);
       // messages.add(i,newMessage);
        notifyDataSetChanged();
    }*/

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case PHOTO:
                View photoView = layoutInflater.inflate(R.layout.item_image_attachment, null);
                return new ImageAttachmentHolder(photoView);

            case LOCATION:
                View locationView = layoutInflater.inflate(R.layout.item_location_attachment, null);
                return new LocationAttachmentHolder(locationView);

            case AUDIO:
                View audioView=layoutInflater.inflate(R.layout.item_audio_attachment,null);
                return new AudioAttachmentHolder(audioView);

            case CALL:
                View callView=layoutInflater.inflate(R.layout.item_call,null);
                callView.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
                return new CallHolder(callView);

            case TEXT:
            default:
                View textView = layoutInflater.inflate(R.layout.item_text_chat, null);
                return new TextChatHolder(textView);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Messages message = (Messages) messages.get(position);
        switch (holder.getItemViewType()) {
            case TEXT:
                ChatLayoutDataBindHelper.configureTextChatHolder(mContext, (TextChatHolder) holder, message);
                break;

            case PHOTO:
                ChatLayoutDataBindHelper.configureImageAttachment(mContext, (ImageAttachmentHolder) holder, message);
                break;

            case LOCATION:
                ChatLayoutDataBindHelper.configureLocation(mContext,(LocationAttachmentHolder) holder,message);
                break;

            case AUDIO:
                ChatLayoutDataBindHelper.configureAudioAttachment(this,mContext,(AudioAttachmentHolder) holder,message);
                break;

            case CALL:
                ChatLayoutDataBindHelper.configureCallView(mContext,(CallHolder)holder, message);
                break;
        }
            //replace the alpha logic with message.getPregress()
            if(message.isInProgress()){
                holder.itemView.setAlpha(0.7f);
            }else{
                holder.itemView.setAlpha(1.0f);
            }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {

        switch (messages.get(position).getMessageType()) {
            case IMAGE:
                return PHOTO;

            case LOCATION:
                return LOCATION;

            case AUDIO:
                return AUDIO;

            case CALL:
                return CALL;

            case TEXT:
            default:
                return TEXT;
        }
    }

    public static class TextChatHolder extends RecyclerView.ViewHolder {
        public TextView lblText, lblTime;

        public TextChatHolder(View itemView) {
            super(itemView);
            lblText = (TextView) itemView.findViewById(R.id.lblText);
            lblTime = (TextView) itemView.findViewById(R.id.lblTime);
        }
    }

    public static class ImageAttachmentHolder extends RecyclerView.ViewHolder {
        public TextView lblTime;
        public ImageView imgAttachment;
        public ProgressBar progressBar;
        public RelativeLayout rlImageView;

        public ImageAttachmentHolder(View itemView) {
            super(itemView);
            imgAttachment = (ImageView) itemView.findViewById(R.id.img_attachment);
            lblTime = (TextView) itemView.findViewById(R.id.lblTime);
            progressBar= (ProgressBar) itemView.findViewById(R.id.progressBar);
            rlImageView= (RelativeLayout) itemView.findViewById(R.id.rl_image);
        }
    }

    public static class LocationAttachmentHolder extends RecyclerView.ViewHolder {
        ImageView imgLocationAttachment;
        TextView lblLocationName, lblLocationDesc,lblTime;
        RelativeLayout rlRoot;
        LinearLayout llInfo;

        public LocationAttachmentHolder(View itemView) {
            super(itemView);
            rlRoot= (RelativeLayout) itemView.findViewById(R.id.rl_location_message);
            llInfo= (LinearLayout) itemView.findViewById(R.id.ll_info);
            imgLocationAttachment = (ImageView) itemView.findViewById(R.id.img_location);
            lblLocationName = (TextView) itemView.findViewById(R.id.lblLocationName);
            lblLocationDesc = (TextView) itemView.findViewById(R.id.lblLocationDesc);
            lblTime = (TextView) itemView.findViewById(R.id.lblTime);
        }
    }

    public static class AudioAttachmentHolder extends RecyclerView.ViewHolder{
        RelativeLayout llRoot;
        TextView lblTime,lblCurrent,lblTotalDuration;
        ImageButton btnDownload;
        SeekBar audioProgress;

        public AudioAttachmentHolder(View itemView) {
            super(itemView);
            llRoot= (RelativeLayout) itemView.findViewById(R.id.ll_root);
            lblTime = (TextView) itemView.findViewById(R.id.lblTime);
            btnDownload= (ImageButton) itemView.findViewById(R.id.btnDownload);
            audioProgress= (SeekBar) itemView.findViewById(R.id.sb_audio_progress);
            lblCurrent= (TextView) itemView.findViewById(R.id.lblCurrent);
            lblTotalDuration= (TextView) itemView.findViewById(R.id.lblTotalDuration);
        }
    }

    public static class CallHolder extends RecyclerView.ViewHolder{

        TextView lblCallMsg;

        public CallHolder(View itemView) {
            super(itemView);
            lblCallMsg= (TextView) itemView.findViewById(R.id.lbl_call_message);
        }
    }
}
