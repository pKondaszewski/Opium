package com.example.op.activity.report;

import android.os.Bundle;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.op.R;
import com.example.op.activity.extra.TranslatedAppCompatActivity;
import com.example.op.adapter.report.ReportReceiversAdapter;
import com.example.op.utils.simple.SimpleOnTabSelectedListener;
import com.google.android.material.tabs.TabLayout;


public class ReportReceiversActivity extends TranslatedAppCompatActivity {

    private ViewPager2 vp2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_receivers);

        FragmentManager fm = getSupportFragmentManager();
        ReportReceiversAdapter vsa = new ReportReceiversAdapter(fm, getLifecycle());
        vp2 = findViewById(R.id.view_pager_report_receivers);
        vp2.setAdapter(vsa);
        TabLayout tabLayout = findViewById(R.id.tab_layout_report);
        tabLayout.addOnTabSelectedListener(new SimpleOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp2.setCurrentItem(tab.getPosition());
            }
        });

        vp2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }
}
