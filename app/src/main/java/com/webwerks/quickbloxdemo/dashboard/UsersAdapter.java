package com.webwerks.quickbloxdemo.dashboard;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.webwerks.qbcore.models.User;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.databinding.UserBinding;

import java.util.List;

/**
 * Created by webwerks on 14/4/17.
 */

public class UsersAdapter extends ArrayAdapter<User> {

    Context mContext;

    public UsersAdapter(@NonNull Context context, @NonNull List<User> objects) {
        super(context, 0, objects);
        mContext=context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        UserBinding binding= DataBindingUtil
                .inflate(LayoutInflater.from(mContext), R.layout.user_list_item,parent,false);
        binding.setUser(getItem(position));
        binding.setViewModel(new UsersViewModel(mContext) );

        return binding.getRoot();
    }
}
