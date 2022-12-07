package com.example.op.adapter.fitbit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.op.fragment.fitbit.spo2.FitbitMonthSpO2Fragment;
import com.example.op.fragment.fitbit.spo2.FitbitDaysSpO2Fragment;

import java.util.List;

public class FitbitSpO2Adapter extends FragmentStateAdapter {

    private final List<Fragment> fragments;

    public FitbitSpO2Adapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        FitbitDaysSpO2Fragment fitbitDaysSpO2Fragment = new FitbitDaysSpO2Fragment();
        FitbitMonthSpO2Fragment fitbitMonthStepsFragment = new FitbitMonthSpO2Fragment();
        fragments = List.of(fitbitDaysSpO2Fragment, fitbitMonthStepsFragment);
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
