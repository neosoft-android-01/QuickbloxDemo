package com.webwerks.quickbloxdemo.chat;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webwerks.qbcore.models.ChatMessages;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.databinding.ReceivedMessageBinding;
import com.webwerks.quickbloxdemo.databinding.SentMessageBinding;
import com.webwerks.quickbloxdemo.global.App;

import java.util.List;

/**
 * Created by webwerks on 24/4/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    List<ChatMessages> messages;
    Context mContext;
    //private final int SENT = 0, RECEIVED = 1;
    private final int TEXT = 0, PHOTO = 1;

    public ChatAdapter(Context context,List<ChatMessages> messagesList){
        mContext=context;
        messages=messagesList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TEXT:
                default:
                View sentView = /*LayoutInflater.from(mContext).inflate(R.layout.chat_send_item, null);*/
                new ChatTextMessage(mContext);
                return new TextChatHolder(sentView);

           /* case PHOTO:
            default:
                *//*View receivedView = LayoutInflater.from(mContext).inflate(R.layout.chat_received_item, null);
                return new SentBindingHolder(receivedView);*//*
                return new View(mContext);*/
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        //holder.getItemViewType()
        switch (holder.getItemViewType()){
            case TEXT:
                ((ChatTextMessage)holder.itemView).setData(messages.get(position));
                //((SentBindingHolder)(holder)).getBinding().setData(messages.get(position));
                break;

            /*case RECEIVED:
                ((ReceivedBindingHolder)(holder)).getBinding().setData(messages.get(position));
                break;*/

        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {
        /*if(messages.get(position).getSenderId().equals(App.getAppInstance().getCurrentUser().id)){
            return SENT;
        }else{
            return RECEIVED;
        }*/

        if(!TextUtils.isEmpty(messages.get(position).getMsg())){
            return TEXT;
        }else{
            return PHOTO;
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


    public static class TextChatHolder extends RecyclerView.ViewHolder{

        public TextChatHolder(View itemView) {
            super(itemView);
        }
    }

}
