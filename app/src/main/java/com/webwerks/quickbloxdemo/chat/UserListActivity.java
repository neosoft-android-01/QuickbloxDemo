package com.webwerks.quickbloxdemo.chat;

import android.widget.ListView;

import com.webwerks.qbcore.models.User;
import com.webwerks.qbcore.user.QbUserAuth;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.model.UserList;
import com.webwerks.quickbloxdemo.ui.activities.BaseActivity;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

/**
 * Created by webwerks on 12/4/17.
 */

public class UserListActivity extends BaseActivity {
    @Override
    public int getContentLayout() {
        return R.layout.activity_userlist;
    }

    @Override
    public void initializeUiComponents() {
        final UserList userList=new UserList();
        App.getAppInstance().showLoading(this);
        QbUserAuth.getUsers().subscribe(new Consumer() {
            @Override
            public void accept(Object o) throws Exception {

                App.getAppInstance().hideLoading();

                ArrayList<User> users= (ArrayList<User>) o;
                userList.setQbUsers(users);

                ListView lst= (ListView) findViewById(R.id.lst_user);
                lst.setAdapter(new UsersAdapter(lst.getContext(),users));

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {

            }
        });
    }

   /* @BindingAdapter("android:entries")
    public static void bindList(ListView v, ObservableArrayList<User> list){
        UsersAdapter usersAdapter=new UsersAdapter(v.getContext(),list);
        v.setAdapter(usersAdapter);
    }*/
}
