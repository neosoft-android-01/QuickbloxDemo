package com.webwerks.quickbloxdemo.model;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.webwerks.quickbloxdemo.BR;

/**
 * Created by webwerks on 7/4/17.
 */

public class CustomQbUser extends BaseObservable {

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    @Bindable
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
        notifyPropertyChanged(BR.email);
    }

    @Bindable
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    @Bindable
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
        notifyPropertyChanged(BR.firstName);
    }

    @Bindable
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
        notifyPropertyChanged(BR.lastName);
    }
}
