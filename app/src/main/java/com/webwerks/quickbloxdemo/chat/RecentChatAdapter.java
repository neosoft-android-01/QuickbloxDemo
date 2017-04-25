package com.webwerks.quickbloxdemo.chat;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.databinding.RecentBinding;

import java.util.List;

/**
 * Created by webwerks on 24/4/17.
 */

public class RecentChatAdapter extends ArrayAdapter<ChatDialog> {

    Context mContext;

    public RecentChatAdapter(@NonNull Context context,@NonNull List<ChatDialog> objects) {
        super(context, 0, objects);
        mContext=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        RecentBinding binding= DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.recent_chat_item,parent,false);
        binding.setChat(getItem(position));
        binding.setViewModel(new UsersViewModel(mContext));
        return binding.getRoot();
    }
}
