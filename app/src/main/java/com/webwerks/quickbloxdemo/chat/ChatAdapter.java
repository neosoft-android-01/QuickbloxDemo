package com.webwerks.quickbloxdemo.chat;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by webwerks on 24/4/17.
 */

public class ChatAdapter extends ArrayAdapter<Object>{
    public ChatAdapter(@NonNull Context context, @NonNull List<Object> objects) {
        super(context, 0, objects);
    }
}
