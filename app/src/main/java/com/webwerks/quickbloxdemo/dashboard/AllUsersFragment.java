package com.webwerks.quickbloxdemo.dashboard;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.widget.ListView;

import com.webwerks.qbcore.models.User;
import com.webwerks.qbcore.user.QbUserAuth;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.databinding.UserListBinding;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.ui.fragment.BaseFragment;
import com.webwerks.quickbloxdemo.utils.ListUtils;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * Created by webwerks on 17/4/17.
 */

public class AllUsersFragment extends BaseFragment<UserListBinding> {

    public static AllUsersFragment newInstance(){
        AllUsersFragment fragment=new AllUsersFragment();
        Bundle args=new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getContentLayout() {
        return R.layout.fragment_all_users;
    }

    @Override
    public void initializeUiComponents(final UserListBinding binding) {
        App.getAppInstance().showLoading(getActivity());
        QbUserAuth.getUsers().subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                App.getAppInstance().hideLoading();
                ArrayList<User> users= (ArrayList<User>) o;
                binding.setUsers(ListUtils.getInstance().convertToObservableList(users));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                App.getAppInstance().hideLoading();
            }
        });
    }



    @BindingAdapter({"bind:items"})
    public static void bindList(ListView v, ObservableArrayList<User> list){
        if(list!=null && list.size()>0) {
            UsersAdapter usersAdapter = new UsersAdapter(v.getContext(), list);
            v.setAdapter(usersAdapter);
        }
    }
}
