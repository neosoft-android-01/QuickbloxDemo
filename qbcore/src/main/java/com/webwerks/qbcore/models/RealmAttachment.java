package com.webwerks.qbcore.models;

import com.quickblox.chat.model.QBAttachment;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by webwerks on 28/4/17.
 */

public class RealmAttachment extends RealmObject {

    @PrimaryKey
    private String id;
    private String data;
    private String type;
    private String url;
    private String contentType;


    public static RealmAttachment getRealmAttachment(QBAttachment qbAttachment){
        RealmAttachment realmAttachment=new RealmAttachment();
        qbAttachment.getContentType();
        realmAttachment.type=qbAttachment.getType();
        realmAttachment.contentType=qbAttachment.getContentType();
        realmAttachment.url=qbAttachment.getUrl();
        realmAttachment.id=qbAttachment.getId();
        realmAttachment.data=qbAttachment.getData();
        return realmAttachment;
    }

    public static QBAttachment getQBAttachment(RealmAttachment realmAttachment){
        QBAttachment qbAttachment=new QBAttachment(realmAttachment.type);
        qbAttachment.setType(realmAttachment.type);
        qbAttachment.setContentType(realmAttachment.contentType);
        qbAttachment.setUrl(realmAttachment.url);
        qbAttachment.setId(realmAttachment.id);
        qbAttachment.setData(realmAttachment.data);
        return qbAttachment;
    }

}
