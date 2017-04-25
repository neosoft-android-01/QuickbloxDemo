package com.webwerks.quickbloxdemo.chat;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.widget.ListView;

import com.webwerks.qbcore.chat.ChatDialogManager;
import com.webwerks.qbcore.models.ChatDialog;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.databinding.RecentChatBinding;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.ui.fragment.BaseFragment;
import com.webwerks.quickbloxdemo.utils.ListUtils;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * Created by webwerks on 24/4/17.
 */

public class RecentChatFragment extends BaseFragment<RecentChatBinding> {

    public static RecentChatFragment newInstance(){
        RecentChatFragment fragment=new RecentChatFragment();
        Bundle args=new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getContentLayout() {
        return R.layout.fragment_recent_chats;
    }

    @Override
    public void initializeUiComponents(final RecentChatBinding binding) {
        App.getAppInstance().showLoading(getActivity());
        ChatDialogManager.getRecentChatDialogs(getContext()).subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                App.getAppInstance().hideLoading();
                ArrayList<ChatDialog> recentList= (ArrayList<ChatDialog>) o;
                binding.setChats(ListUtils.getInstance().convertToObservableList(recentList));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                App.getAppInstance().hideLoading();
            }
        }) ;

    }

    @BindingAdapter({"bind:items"})
    public static void bindList(ListView v, ObservableArrayList<ChatDialog> list){
        if(list!=null && list.size()>0){
            RecentChatAdapter usersAdapter = new RecentChatAdapter(v.getContext(), list);
            v.setAdapter(usersAdapter);
        }
    }
}
