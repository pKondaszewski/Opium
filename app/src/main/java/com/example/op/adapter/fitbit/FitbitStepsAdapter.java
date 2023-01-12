package com.example.op.adapter.fitbit;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.op.fragment.fitbit.steps.FitbitMonthsStepsFragment;
import com.example.op.fragment.fitbit.steps.FitbitDaysStepsFragment;

import java.util.List;


public class FitbitStepsAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragments;

    public FitbitStepsAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        FitbitDaysStepsFragment fitbitDaysStepsFragment = new FitbitDaysStepsFragment();
        FitbitMonthsStepsFragment fitbitMonthsStepsFragment = new FitbitMonthsStepsFragment();
        fragments = List.of(fitbitDaysStepsFragment, fitbitMonthsStepsFragment);
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
