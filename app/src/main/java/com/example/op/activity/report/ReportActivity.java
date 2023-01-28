package com.example.op.activity.report;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.op.R;
import com.example.op.activity.extra.GlobalSetupAppCompatActivity;
import com.example.op.adapter.report.ReportAdapter;
import com.example.op.utils.Start;
import com.example.op.utils.simple.SimpleOnTabSelectedListener;
import com.google.android.material.tabs.TabLayout;

public class ReportActivity extends GlobalSetupAppCompatActivity implements MenuItem.OnMenuItemClickListener {
    private final String[] PERMISSIONS = new String[] {Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS};

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_report, menu);
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setOnMenuItemClickListener(this);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        setupTableLayout();
        for (String permission : PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS, 100);
                return;
            }
        }
    }

    private void setupTableLayout() {
        FragmentManager fm = getSupportFragmentManager();

        ReportAdapter vsa = new ReportAdapter(fm, getLifecycle());
        ViewPager2 vp2 = findViewById(R.id.view_pager_report_receivers);
        TabLayout tabLayout = findViewById(R.id.tab_layout_report);
        vp2.setAdapter(vsa);
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

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.settingsReportMenuItem) {
            Start.activity(this, ReportReceiversActivity.class);
        }
        return false;
    }
}