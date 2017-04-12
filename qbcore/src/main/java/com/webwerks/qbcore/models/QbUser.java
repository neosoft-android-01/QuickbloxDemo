package com.webwerks.qbcore.models;

import com.google.gson.annotations.SerializedName;
import com.quickblox.users.model.QBUser;

import java.util.Date;

import io.realm.RealmObject;

/**
 * Created by webwerks on 12/4/17.
 */

public class QbUser extends RealmObject{
    
    public QbUser(){}

    public int id;
    public String fullName;
    public String email;
    public String login;
    public String phone;
    public String website;
    public Date lastRequestAt;
    public String externalId;
    public String facebookId;
    public String twitterId;
    public String twitterDigitsId;
    public Integer blobId;
    public String tags;
    public String password;
    public String oldPassword;
    public String customData;



    public static QbUser fromQbUser(QBUser qbUser){
        
        QbUser thisU = new QbUser();
        thisU.id=qbUser.getId();
        thisU.fullName=qbUser.getFullName();
        thisU.email=qbUser.getEmail();
        thisU.login=qbUser.getLogin();
        thisU.phone=qbUser.getPhone();
        thisU.website=qbUser.getWebsite();
        thisU.lastRequestAt=qbUser.getLastRequestAt();
        thisU.externalId=qbUser.getExternalId();
        thisU.facebookId=qbUser.getFacebookId();
        thisU.twitterId=qbUser.getTwitterId();
        thisU.twitterDigitsId=qbUser.getTwitterDigitsId();
        thisU.blobId=qbUser.getFileId();
        //thisU.tags=qbUser.getTags();
        thisU.password=qbUser.getPassword();
        thisU.oldPassword=qbUser.getOldPassword();
        thisU.customData=qbUser.getCustomData();
        //thisU.customDataClass= (Class) qbUser.getCustomDataAsObject();

        return thisU;
    }



}
