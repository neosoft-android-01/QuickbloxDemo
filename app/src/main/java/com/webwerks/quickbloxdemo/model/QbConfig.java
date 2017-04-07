package com.webwerks.quickbloxdemo.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by webwerks on 5/4/17.
 */

public class QbConfig{

    public QbConfig() {
    }

    @SerializedName("app_id")
    @Expose
    private String appId;
    @SerializedName("auth_key")
    @Expose
    private String authKey;
    @SerializedName("auth_secret")
    @Expose
    private String authSecret;
    @SerializedName("account_key")
    @Expose
    private String accountKey;

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAuthKey() {
        return authKey;
    }

    public void setAuthKey(String authKey) {
        this.authKey = authKey;
    }

    public String getAuthSecret() {
        return authSecret;
    }

    public void setAuthSecret(String authSecret) {
        this.authSecret = authSecret;
    }

    public String getAccountKey() {
        return accountKey;
    }

    public void setAccountKey(String accountKey) {
        this.accountKey = accountKey;
    }
}
