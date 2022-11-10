package com.example.op.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.op.R;
import com.example.database.AppDatabase;
import com.example.database.entity.FitbitStepsData;
import com.example.op.domain.FitbitDataUseCase;
import com.example.op.utils.ChartUtils;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;

import java.util.List;

public class FitbitWeekStepsFragment extends Fragment {

    private static final int weekValue = 7;

    AppDatabase database;
    FitbitDataUseCase fitbitDataUseCase;
    BarChart barChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        database = AppDatabase.getDatabaseInstance(getContext());
        fitbitDataUseCase = new FitbitDataUseCase();
        return inflater.inflate(R.layout.fragment_week_chart_fitbit_steps_data, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        barChart = (BarChart) view.findViewById(R.id.barChart);
        generateFitbitStepsDataChart(7);
    }

    public void generateFitbitStepsDataChart(int dayLimit) {
        List<FitbitStepsData> stepsData = database.fitbitStepsDataDao().getHighestFitbitStepsDataValueByEveryDate(dayLimit);
        BarData barData;
        if (stepsData.isEmpty()) {
            barData = null;
        } else {
            barData = fitbitDataUseCase.getFitbitStepsBarData(stepsData);
        }
        barChart.setData(barData);
        ChartUtils.generateBarChart(barChart, weekValue);
    }
}
