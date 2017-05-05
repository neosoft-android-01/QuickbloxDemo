package com.webwerks.quickbloxdemo.chat;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.webwerks.qbcore.models.Messages;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.utils.DateUtils;
import com.webwerks.quickbloxdemo.utils.FileUtil;

import io.reactivex.functions.Consumer;

/**
 * Created by webwerks on 27/4/17.
 */

public class ChatLayoutDataBindHelper {

    public static void configureTextViewHolder(Context context, ChatAdapter.TextChatHolder holder, Messages message){
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

    public static void configureImageAttachment(final Context context,final ChatAdapter.PhotoChatHolder holder,final Messages message){
        boolean sent=true;
        if(message.getSenderId()!=null) {
            if (message.getSenderId() == App.getAppInstance().getCurrentUser().id)
                sent = true;
            else
                sent = false;
        }

        FileUtil.getImageAttachmentPath(Integer.parseInt(message.getAttachments().get(0).getId()))
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
        RelativeLayout.LayoutParams params=new RelativeLayout.LayoutParams(300, 350);
        params.topMargin=10;

        RelativeLayout.LayoutParams timeParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        timeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        if(sent){
            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            timeParams.addRule(RelativeLayout.LEFT_OF, R.id.img_attachment);
            timeParams.rightMargin=30;
        }else{
            params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            timeParams.addRule(RelativeLayout.RIGHT_OF,R.id.img_attachment);
            timeParams.leftMargin=30;
        }
        holder.imgAttachment.setLayoutParams(params);
        holder.lblTime.setLayoutParams(timeParams);
    }

    public static void configureLocation(final Context context, final ChatAdapter.LocationChatHolder holder, final Messages message){

    }
}
