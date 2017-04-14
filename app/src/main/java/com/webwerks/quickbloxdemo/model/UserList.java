package com.webwerks.quickbloxdemo.model;

import android.databinding.ObservableArrayList;

import com.webwerks.qbcore.models.User;

import java.util.ArrayList;

/**
 * Created by webwerks on 14/4/17.
 */

public class UserList {

    ObservableArrayList<User> qbUsers=new ObservableArrayList<>();

    public ArrayList<User> getQbUsers() {
        return qbUsers;
    }

    public void setQbUsers(ArrayList<User> qbUsers) {
        this.qbUsers = (ObservableArrayList<User>) qbUsers;
    }


/*
    public void UserList(ArrayList<QbUser> qbUsers){
        this.qbUsers=qbUsers;
    }*/

}
