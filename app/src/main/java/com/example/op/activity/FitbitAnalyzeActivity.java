package com.example.op.activity;

import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.database.AppDatabase;
import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;
import com.example.op.R;
import com.example.op.adapter.fitbit.FitbitStepsAdapter;
import com.example.op.domain.FitbitDataUseCase;
import com.example.op.utils.Authorization;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.google.android.material.tabs.TabLayout;

import java.time.LocalDate;
import java.util.List;

import okhttp3.Authenticator;
import okhttp3.OkHttpClient;

public class FitbitAnalyzeActivity extends AppCompatActivity implements View.OnClickListener {

    private BarChart barChart;
    private BarChart barChart1;

    TextView textView;
    private OkHttpClient httpClient;
    String activityUrl, activityUrl2, authorizationToken;
    AppDatabase database;
    Authorization authorization;
    LocalDate currentDate = LocalDate.now();

    Authenticator authenticator;

    FitbitDataUseCase fitbitDataUseCase;

    private final String TAG = FitbitAnalyzeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_fitbit);

        barChart = (BarChart) findViewById(R.id.barChart);
        barChart1 = (BarChart) findViewById(R.id.barChart1);

        FragmentManager fm = getSupportFragmentManager();
        FitbitStepsAdapter vsa = new FitbitStepsAdapter(fm, getLifecycle());
        final ViewPager2 vp2 = findViewById(R.id.pager);
        vp2.setAdapter(vsa);

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                vp2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        vp2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("  GfG | Action Bar");
        //actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        database = AppDatabase.getDatabaseInstance(this);
        fitbitDataUseCase = new FitbitDataUseCase();

        generateFitbitStepsDataChart(7);
//        generateCharts();
    }

    private void generateFitbitStepsDataChart(int dayLimit) {
        List<FitbitStepsData> stepsData = database.fitbitStepsDataDao().getHighestFitbitStepsDataValueByEveryDate(7);
        BarData barData;
        if (stepsData.isEmpty()) {
            barData = null;
        } else {
            barData = fitbitDataUseCase.getFitbitStepsBarData(stepsData);
        }
        barChart.setData(barData);

        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        barChart.getXAxis().setGranularity(stepsData.size());

        barChart.setNoDataText("Brak danych");
        barChart.invalidate();
    }

    private void generateCharts() {
        List<FitbitStepsData> stepsData = database.fitbitStepsDataDao().getHighestFitbitStepsDataValueByEveryDate(7);
        BarData barData;
        if (stepsData.isEmpty()) {
            barData = null;
        } else {
            barData = fitbitDataUseCase.getFitbitStepsBarData(stepsData);
        }
        barChart.setData(barData);

        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        barChart.getXAxis().setGranularity(stepsData.size());

        barChart.setNoDataText("Brak danych");
        barChart.invalidate();

        List<FitbitSpO2Data> spO2Data = database.fitbitSpO2DataDao().getHighestFitbitSpO2DataValueByEveryDate();

        BarData barData2;
        if (spO2Data.isEmpty()) {
            barData2 = null;
        } else {
            barData2 = fitbitDataUseCase.getFitbitSpO2BarData(spO2Data);
        }
        barChart1.setData(barData2);

        barChart1.getDescription().setEnabled(false);
        barChart1.getAxisRight().setEnabled(false);
        barChart1.getAxisLeft().setEnabled(false);
        barChart1.getLegend().setEnabled(false);

        barChart1.getXAxis().setDrawGridLines(false);
        barChart1.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        barChart1.getXAxis().setGranularity(spO2Data.size());

        barChart1.setNoDataText("Brak danych");
        barChart1.invalidate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_analyze, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
    }
}