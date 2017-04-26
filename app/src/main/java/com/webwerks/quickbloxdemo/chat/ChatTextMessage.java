package com.webwerks.quickbloxdemo.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webwerks.qbcore.models.ChatMessages;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.global.App;

/**
 * Created by webwerks on 26/4/17.
 */

public class ChatTextMessage extends LinearLayout {

    ChatMessages chatMessages;

    public ChatTextMessage(@NonNull Context context/*, ChatMessages messages*/) {
        super(context);
        //chatMessages=messages;
        init(context);
    }

    public void setData(ChatMessages messages){
        chatMessages=messages;
    }

    public void init(Context context){

        LinearLayout.LayoutParams msgParams=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        msgParams.weight=7;
        if(chatMessages.getSenderId()== App.getAppInstance().getCurrentUser().id){
            msgParams.gravity=Gravity.RIGHT;
        }else{
            msgParams.gravity=Gravity.LEFT;
        }

        LinearLayout.LayoutParams timeprams=new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        msgParams.weight=3;
        if(chatMessages.getSenderId()== App.getAppInstance().getCurrentUser().id){
            msgParams.gravity=Gravity.RIGHT;
        }else{
            msgParams.gravity=Gravity.LEFT;
        }

        TextView lblMessage=new TextView(context);
        lblMessage.setLayoutParams(msgParams);
        lblMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP,16);
        lblMessage.setText(chatMessages.getMsg());
        lblMessage.setGravity(Gravity.CENTER);
        lblMessage.setPadding(5,5,5,5);
        lblMessage.setBackgroundResource(R.drawable.sent_msg_bg);
        lblMessage.setTextColor(getResources().getColor(android.R.color.white));

        TextView lblTime=new TextView(context);
        lblMessage.setLayoutParams(timeprams);
        lblMessage.setTextSize(TypedValue.COMPLEX_UNIT_SP,14);
        lblMessage.setText(chatMessages.getDateSent()+"");
        lblMessage.setGravity(Gravity.CENTER);
        lblMessage.setPadding(5,5,5,5);
        lblMessage.setTextColor(getResources().getColor(android.R.color.white));

        setOrientation(HORIZONTAL);
        setWeightSum(10);
        addView(lblMessage);
        addView(lblTime);

    }

}
