package com.webwerks.quickbloxdemo.chat;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.webwerks.qbcore.models.Messages;
import com.webwerks.quickbloxdemo.R;

import java.util.List;

/**
 * Created by webwerks on 24/4/17.
 */

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int TEXT = 0, PHOTO = 1, LOCATION = 2;
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        switch (viewType) {
            case PHOTO:
                View photoView = layoutInflater.inflate(R.layout.item_photo_msg, null);
                return new PhotoChatHolder(photoView);

            case LOCATION:
                View locationView = layoutInflater.inflate(R.layout.item_location_msg, null);
                return new LocationChatHolder(locationView);

            case TEXT:
            default:
                View textView = layoutInflater.inflate(R.layout.item_text_chat, null);
                return new TextChatHolder(textView);

        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Messages message = messages.get(position);
        switch (holder.getItemViewType()) {
            case TEXT:
                ChatLayoutDataBindHelper.configureTextViewHolder(mContext, (TextChatHolder) holder, message);
                break;

            case PHOTO:
                ChatLayoutDataBindHelper.configureImageAttachment(mContext, (PhotoChatHolder) holder, message);
                break;

            case LOCATION:
                ChatLayoutDataBindHelper.configureLocation(mContext,(LocationChatHolder) holder,message);
                break;
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

    public static class PhotoChatHolder extends RecyclerView.ViewHolder {
        public TextView lblTime;
        public ImageView imgAttachment;

        public PhotoChatHolder(View itemView) {
            super(itemView);
            imgAttachment = (ImageView) itemView.findViewById(R.id.img_attachment);
            lblTime = (TextView) itemView.findViewById(R.id.lblTime);
        }
    }

    public static class LocationChatHolder extends RecyclerView.ViewHolder {
        ImageView imgLocationAttachment;
        TextView lblLocationName, lblLocationDesc;

        public LocationChatHolder(View itemView) {
            super(itemView);
            imgLocationAttachment = (ImageView) itemView.findViewById(R.id.img_location);
            lblLocationName = (TextView) itemView.findViewById(R.id.lblLocationName);
            lblLocationDesc = (TextView) itemView.findViewById(R.id.lblLocationDesc);
        }
    }
}
