package com.webwerks.quickbloxdemo.utils;

import android.databinding.ObservableArrayList;

import com.webwerks.qbcore.models.User;

import java.util.ArrayList;

import io.realm.RealmObject;

/**
 * Created by webwerks on 24/4/17.
 */

public class ListUtils<T extends RealmObject> {

    private static ListUtils instance;

    public static ListUtils getInstance(){
        if(instance==null)
            instance=new ListUtils();
        return instance;
    }

    public ObservableArrayList<T> convertToObservableList(ArrayList<T> list){
        ObservableArrayList<T> observableArrayList=new ObservableArrayList<>();
        for(T obj:list){
            observableArrayList.add(obj);
        }
        return observableArrayList;
    }

}
