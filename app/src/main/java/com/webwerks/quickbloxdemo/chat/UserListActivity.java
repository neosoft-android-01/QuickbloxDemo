package com.webwerks.quickbloxdemo.chat;

import com.webwerks.qbcore.user.QbUserAuth;
import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.global.App;
import com.webwerks.quickbloxdemo.ui.activities.BaseActivity;

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
        QbUserAuth.getUsers();
    }
}
