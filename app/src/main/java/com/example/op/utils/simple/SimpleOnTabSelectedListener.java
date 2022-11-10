package com.example.op.utils.simple;

import com.google.android.material.tabs.TabLayout;

public abstract class SimpleOnTabSelectedListener implements TabLayout.OnTabSelectedListener {

    public abstract void onTabSelected(TabLayout.Tab tab);
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {}
    @Override
    public void onTabReselected(TabLayout.Tab tab) {}
}
