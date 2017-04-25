package com.webwerks.quickbloxdemo.chat;

import android.databinding.BindingAdapter;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import com.webwerks.quickbloxdemo.R;
import com.webwerks.quickbloxdemo.databinding.DashboardBinding;
import com.webwerks.quickbloxdemo.ui.activities.BaseActivity;

/**
 * Created by webwerks on 24/4/17.
 */

public class DashboardActivity extends BaseActivity<DashboardBinding> {
    @Override
    public int getContentLayout() {
        return R.layout.activity_dashboard;
    }

    @Override
    public void initializeUiComponents(DashboardBinding binding) {
        binding.setActivity(this);
        binding.setManager(getSupportFragmentManager());
    }
    @BindingAdapter({"bind:tabs"})
    public static void bindViewPagerAdapter(final ViewPager view, final DashboardActivity activity) {
        final DashboardTabAdapter adapter = new DashboardTabAdapter(activity.getSupportFragmentManager());
        adapter.addFragment(RecentChatFragment.newInstance(),"RECENT");
        adapter.addFragment(AllUsersFragment.newInstance(),"ALL CONTACT");
        view.setAdapter(adapter);
    }

    @BindingAdapter({"bind:pager"})
    public static void bindViewPagerTabs(final TabLayout view, final ViewPager pagerView) {
        view.setupWithViewPager(pagerView);
    }
}
