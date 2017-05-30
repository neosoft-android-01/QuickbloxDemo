package com.webwerks.quickbloxdemo.ui.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webwerks.quickbloxdemo.R;

/**
 * Created by webwerks on 24/4/17.
 */

public abstract class BaseFragment<T extends ViewDataBinding> extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(getContentLayout(),null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        T binding= DataBindingUtil.bind(view);
        initializeUiComponents(binding);
    }

    public abstract int getContentLayout();

    public abstract void initializeUiComponents(T binding);
}
