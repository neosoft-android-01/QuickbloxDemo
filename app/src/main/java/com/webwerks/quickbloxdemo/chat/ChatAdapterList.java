package com.webwerks.quickbloxdemo.chat;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.webwerks.qbcore.models.ChatMessages;
import com.webwerks.quickbloxdemo.R;

import java.util.List;

/**
 * Created by webwerks on 26/4/17.
 */

public class ChatAdapterList extends ArrayAdapter<ChatMessages> {

    List<ChatMessages> messages;
    private final int TEXT = 0, PHOTO = 1;
    Context mContext;

    public ChatAdapterList(@NonNull Context context, @NonNull List<ChatMessages> objects) {
        super(context, 0, objects);
        messages=objects;
        mContext=context;
    }

    @Override
    public int getItemViewType(int position) {

        if(!TextUtils.isEmpty(messages.get(position).getMsg())){
            return TEXT;
        }else{
            return PHOTO;
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView= LayoutInflater.from(mContext).inflate(R.layout.item_chat,null);
        View view=null;
        switch (getItemViewType(position)){
            case TEXT:
                view=new ChatTextMessage(mContext,getItem(position));
                break;

            default:
                break;
        }

        LinearLayout root= (LinearLayout) convertView.findViewById(R.id.ll_root);
        root.addView(view);

        //((TextView)convertView.findViewById(R.id.lbl_sent)).setText(getItem(position).getMsg());

        return convertView;
    }
}
