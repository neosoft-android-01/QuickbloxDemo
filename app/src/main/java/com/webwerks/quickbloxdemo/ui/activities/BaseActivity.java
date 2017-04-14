package com.webwerks.quickbloxdemo.ui.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.webwerks.quickbloxdemo.R;

/**
 * Created by webwerks on 7/4/17.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getContentLayout();
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_view));

        initializeUiComponents();


    }

    public abstract int getContentLayout();

    public abstract void initializeUiComponents();

}
