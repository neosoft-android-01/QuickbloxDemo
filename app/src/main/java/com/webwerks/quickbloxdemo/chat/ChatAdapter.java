package com.webwerks.quickbloxdemo.chat;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.webwerks.qbcore.models.ChatMessages;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.utils.DateUtils;

import java.util.List;

/**
 * Created by webwerks on 24/4/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    List<ChatMessages> messages;
    Context mContext;
    private final int TEXT = 0, PHOTO = 1;

    public ChatAdapter(Context context,List<ChatMessages> messagesList){
        mContext=context;
        messages=messagesList;
    }

    public void add(ChatMessages msg){
        messages.add(msg);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(mContext);
        switch (viewType) {
            case PHOTO:
                View photoView=layoutInflater.inflate(R.layout.item_photo_msg,null);
                return new PhotoChatHolder(photoView);

            case TEXT:
                default:
                View textView=layoutInflater.inflate(R.layout.item_text_chat,null);
                return new TextChatHolder(textView);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatMessages message=messages.get(position);
        switch (holder.getItemViewType()){
            case TEXT:
                ChatLayoutDataBindHelper.configureTextViewHolder(mContext,(TextChatHolder) holder,message);
                break;

            case PHOTO:
                ChatLayoutDataBindHelper.configureImageAttachment(mContext, (PhotoChatHolder) holder,message);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {

        if(messages.get(position).getAttachments()!=null && messages.get(position).getAttachments().size()>0){
            return PHOTO;
        }else if(!TextUtils.isEmpty(messages.get(position).getMsg())){
            return TEXT;
        }else{
            return TEXT;
        }
    }

    public static class TextChatHolder extends RecyclerView.ViewHolder{
        public TextView lblText,lblTime;
        public TextChatHolder(View itemView) {
            super(itemView);
            lblText= (TextView) itemView.findViewById(R.id.lblText);
            lblTime= (TextView) itemView.findViewById(R.id.lblTime);
        }
    }

    public static class PhotoChatHolder extends RecyclerView.ViewHolder{
        public TextView lblTime;
        public ImageView imgAttachment;
        public PhotoChatHolder(View itemView) {
            super(itemView);
            imgAttachment= (ImageView) itemView.findViewById(R.id.img_attachment);
            lblTime= (TextView) itemView.findViewById(R.id.lblTime);
        }
    }

    /*public static class SentBindingHolder extends RecyclerView.ViewHolder{
        private SentMessageBinding binding;
        public SentBindingHolder(View itemView) {
            super(itemView);
            binding= DataBindingUtil.bind(itemView);
        }

        public SentMessageBinding getBinding(){
            return binding;
        }
    }

    public static class ReceivedBindingHolder extends RecyclerView.ViewHolder {
        private ReceivedMessageBinding binding;
        public ReceivedBindingHolder(View itemView) {
            super(itemView);
            binding=DataBindingUtil.bind(itemView);
        }

        public ReceivedMessageBinding getBinding(){
            return binding;
        }
    }*/



}
