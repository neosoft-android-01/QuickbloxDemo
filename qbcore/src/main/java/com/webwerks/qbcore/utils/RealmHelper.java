package com.webwerks.qbcore.utils;

import com.webwerks.qbcore.models.RealmInteger;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by webwerks on 25/4/17.
 */

public class RealmHelper {

     public static RealmList<RealmInteger> getIntegerRelamList(List<Integer> list){
        RealmList<RealmInteger> realmList=new RealmList<>();
        for(Integer integer:list){
            RealmInteger realmInteger=new RealmInteger();
            realmInteger.setId(integer);
            realmList.add(realmInteger);
        }
        return realmList;
    }

     public static List<Integer> fromIntegerRelamList(RealmList<RealmInteger> realmList){
        List<Integer> list=new ArrayList<>();
        for(RealmInteger integer:realmList){
            list.add(integer.getId());
        }
        return list;
    }

}
