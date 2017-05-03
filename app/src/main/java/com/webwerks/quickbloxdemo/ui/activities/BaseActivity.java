package com.webwerks.quickbloxdemo.ui.activities;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.webwerks.quickbloxdemo.R;

/**
 * Created by webwerks on 7/4/17.
 */

public abstract class BaseActivity<T extends ViewDataBinding> extends AppCompatActivity  {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(getContentLayout());
        T binding= DataBindingUtil.setContentView(this,getContentLayout());
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_view));

        initializeUiComponents(binding);
    }

    public void setToolbarTitle(String title){
        if((Toolbar) findViewById(R.id.toolbar_view)!=null){
            TextView lblTitle= (TextView) ((Toolbar) findViewById(R.id.toolbar_view)).findViewById(R.id.lbl_title);
            lblTitle.setText(title);
        }
    }

    public abstract int getContentLayout();

    public abstract void initializeUiComponents(T binding);

}
