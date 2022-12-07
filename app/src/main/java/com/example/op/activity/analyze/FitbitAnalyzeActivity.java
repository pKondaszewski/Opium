package com.example.op.activity.analyze;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.op.R;
import com.example.op.adapter.fitbit.FitbitSpO2Adapter;
import com.example.op.adapter.fitbit.FitbitStepsAdapter;
import com.example.op.utils.simple.SimpleOnTabSelectedListener;
import com.google.android.material.tabs.TabLayout;

public class FitbitAnalyzeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_fitbit);

        FragmentManager fm = getSupportFragmentManager();

        FitbitStepsAdapter vsa = new FitbitStepsAdapter(fm, getLifecycle());
        final ViewPager2 stepsVp2 = findViewById(R.id.pager_fitbit_steps);
        stepsVp2.setAdapter(vsa);
        stepsVp2.setUserInputEnabled(false);
        TabLayout fitbitStepsTabLayout = findViewById(R.id.tab_layout_fitbit_steps);
        fitbitStepsTabLayout.addOnTabSelectedListener(new SimpleOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                stepsVp2.setCurrentItem(tab.getPosition());
            }
        });
        stepsVp2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                fitbitStepsTabLayout.selectTab(fitbitStepsTabLayout.getTabAt(position));
            }
        });

        FitbitSpO2Adapter vso2a = new FitbitSpO2Adapter(fm, getLifecycle());
        final ViewPager2 spO2Vp2 = findViewById(R.id.pager_fitbit_spo2);
        spO2Vp2.setAdapter(vso2a);
        spO2Vp2.setUserInputEnabled(false);
        TabLayout fitbitSpO2TabLayout = findViewById(R.id.tab_layout_fitbit_spo2);
        fitbitSpO2TabLayout.addOnTabSelectedListener(new SimpleOnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                spO2Vp2.setCurrentItem(tab.getPosition());
            }
        });
        spO2Vp2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                fitbitSpO2TabLayout.selectTab(fitbitSpO2TabLayout.getTabAt(position));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_fitbit_analyze, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_settings) {
            Intent intent = new Intent(this, FitbitAnalyzeSettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}