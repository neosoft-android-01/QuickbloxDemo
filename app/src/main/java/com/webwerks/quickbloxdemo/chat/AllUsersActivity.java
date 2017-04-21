package com.webwerks.quickbloxdemo.chat;

import android.databinding.BindingAdapter;
import android.databinding.ObservableArrayList;
import android.widget.ListView;

import com.webwerks.qbcore.models.User;
import com.webwerks.qbcore.user.QbUserAuth;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.databinding.UserListBinding;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.ui.activities.BaseActivity;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * Created by webwerks on 17/4/17.
 */

public class AllUsersActivity extends BaseActivity<UserListBinding> {

    @Override
    public int getContentLayout() {
        return R.layout.activity_all_users;
    }

    @Override
    public void initializeUiComponents(final UserListBinding binding) {
        App.getAppInstance().showLoading(this);
        QbUserAuth.getUsers().subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {
                App.getAppInstance().hideLoading();
                ArrayList<User> users= (ArrayList<User>) o;
                binding.setUsers(convertToObservableList(users));
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }

    public ObservableArrayList<User> convertToObservableList(ArrayList<User> users){
        ObservableArrayList<User> qbUsers=new ObservableArrayList<>();
        for(User user:users){
            qbUsers.add(user);
        }
         return qbUsers;
    }

    @BindingAdapter({"bind:items"})
    public static void bindList(ListView v, ObservableArrayList<User> list){
        if(list!=null && list.size()>0) {
            UsersAdapter usersAdapter = new UsersAdapter(v.getContext(), list);
            v.setAdapter(usersAdapter);
        }
    }
}
