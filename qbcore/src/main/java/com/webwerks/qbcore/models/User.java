package com.webwerks.qbcore.models;

import com.quickblox.users.model.QBUser;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by webwerks on 12/4/17.
 */

public class User extends RealmObject{
    
    public User(){}

    @PrimaryKey
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



    public static User fromQbUser(QBUser qbUser){
        
        User thisU = new User();
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

    public static QBUser toQBUser(User user){
        QBUser qbUser=new QBUser();
        qbUser.setId(user.id);
        qbUser.setFullName(user.fullName);
        qbUser.setEmail(user.email);
        qbUser.setLogin(user.login);
        qbUser.setPhone(user.phone);
        qbUser.setWebsite(user.website);
        qbUser.setLastRequestAt(user.lastRequestAt);
        qbUser.setExternalId(user.externalId);
        qbUser.setFacebookId(user.facebookId);
        qbUser.setTwitterId(user.twitterId);
        qbUser.setTwitterDigitsId(user.twitterDigitsId);
        qbUser.setFileId(user.blobId);
        //thisU.tags=qbUser.getTags();
        qbUser.setPassword(user.password);
        qbUser.setOldPassword(user.oldPassword);
        qbUser.setCustomData(user.customData);
        //thisU.customDataClass= (Class) qbUser.getCustomDataAsObject();
        return qbUser;
    }

}
