package com.webwerks.quickbloxdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.webwerks.qbcore.models.User;
import com.webwerks.quickbloxdemo.global.App;

/**
 * Created by webwerks on 11/4/17.
 */

public class SharedPrefUtils {

    private static final String SHARED_PREFS_NAME = "qb";

    private static final String QB_USER_ID = "qb_user_id";
    private static final String QB_USER_EMAIL = "qb_user_email";
    private static final String QB_USER_PASSWORD = "qb_user_password";
    private static final String QB_USER_FULL_NAME = "qb_user_full_name";

    private static SharedPrefUtils instance;

    private SharedPreferences sharedPreferences;

    public static synchronized SharedPrefUtils getInstance() {
        if (instance == null) {
            instance = new SharedPrefUtils();
        }
        return instance;
    }

    private SharedPrefUtils() {
        instance = this;
        sharedPreferences = App.getAppInstance().getSharedPreferences(SHARED_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void save(String key, Object value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        } else if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Enum) {
            editor.putString(key, value.toString());
        }
        editor.commit();
    }

    public void delete(String key) {
        if (sharedPreferences.contains(key)) {
            sharedPreferences.edit().remove(key).commit();
        }
    }

    public void clearAllData(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear().commit();
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) sharedPreferences.getAll().get(key);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key, T defValue) {
        T returnValue = (T) sharedPreferences.getAll().get(key);
        return returnValue == null ? defValue : returnValue;
    }


    public boolean has(String key) {
        return sharedPreferences.contains(key);
    }

    public void saveQbUser(User qbUser) {
        save(QB_USER_ID, qbUser.id);
        save(QB_USER_EMAIL, qbUser.email);
        save(QB_USER_PASSWORD, qbUser.password);
        save(QB_USER_FULL_NAME, qbUser.email);
    }

    public void removeQbUser() {
        delete(QB_USER_ID);
        delete(QB_USER_EMAIL);
        delete(QB_USER_PASSWORD);
        delete(QB_USER_FULL_NAME);
    }

    public boolean hasQbUser() {
        return has(QB_USER_EMAIL) && has(QB_USER_PASSWORD);
    }

    public User getQbUser() {
        if (hasQbUser()) {
            Integer id = get(QB_USER_ID);
            String email=get(QB_USER_EMAIL);
            String password = get(QB_USER_PASSWORD);
            String fullName = get(QB_USER_FULL_NAME);

            User user = new User();
            user.id=id;
            user.email=email;
            user.fullName=fullName;
            user.password=password;

            return user;
        } else {
            return null;
        }
    }
}



