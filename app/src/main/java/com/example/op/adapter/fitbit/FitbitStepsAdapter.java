package com.example.op.adapter.fitbit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.op.fragment.FitbitMonthStepsFragment;
import com.example.op.fragment.FitbitWeekStepsFragment;

import java.util.List;


public class FitbitStepsAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragments;

    public FitbitStepsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        FitbitWeekStepsFragment fitbitWeekStepsFragment = new FitbitWeekStepsFragment();
        FitbitMonthStepsFragment fitbitMonthStepsFragment = new FitbitMonthStepsFragment();
        fragments = List.of(fitbitWeekStepsFragment, fitbitMonthStepsFragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return fragments.get(0);
        } else {
            return fragments.get(1);
        }
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
