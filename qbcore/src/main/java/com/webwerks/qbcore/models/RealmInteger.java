package com.webwerks.qbcore.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by webwerks on 25/4/17.
 */

public class RealmInteger extends RealmObject {
    @PrimaryKey
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
