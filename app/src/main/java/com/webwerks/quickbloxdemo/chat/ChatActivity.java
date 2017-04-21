package com.webwerks.quickbloxdemo.chat;

import android.content.Intent;
import android.databinding.ViewDataBinding;
import android.util.Log;

import com.quickblox.chat.model.QBChatDialog;
import com.webwerks.qbcore.chat.ChatManager;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.ui.activities.BaseActivity;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * Created by webwerks on 17/4/17.
 */

public class ChatActivity extends BaseActivity {

    public void start(){
        startActivity(new Intent(App.getAppInstance(),ChatActivity.class));
    }

    @Override
    public int getContentLayout() {
        return R.layout.activity_chat;
    }

    @Override
    public void initializeUiComponents(ViewDataBinding binding) {



    }
}
