package com.example.op.adapter.report;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.op.fragment.report.receivers.EmailReportReceiversFragment;
import com.example.op.fragment.report.receivers.SmsReportReceiversFragment;

import java.util.List;

public class ReportReceiversAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragments;

    public ReportReceiversAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        SmsReportReceiversFragment smsReportReceiversFragment = new SmsReportReceiversFragment();
        EmailReportReceiversFragment emailReportReceiversFragment = new EmailReportReceiversFragment();
        fragments = List.of(smsReportReceiversFragment, emailReportReceiversFragment);
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
