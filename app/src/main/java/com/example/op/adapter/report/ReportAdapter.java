package com.example.op.adapter.report;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.op.fragment.report.DetailedReportFragment;
import com.example.op.fragment.report.GeneralReportFragment;

import java.util.List;

public class ReportAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragments;

    public ReportAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        GeneralReportFragment generalReportFragment = new GeneralReportFragment();
        DetailedReportFragment detailedReportFragment = new DetailedReportFragment();
        fragments = List.of(generalReportFragment, detailedReportFragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
