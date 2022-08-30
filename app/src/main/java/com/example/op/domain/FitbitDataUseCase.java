package com.example.op.domain;

import com.example.op.database.entity.FitbitSpO2Data;
import com.example.op.database.entity.FitbitStepsData;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class FitbitDataUseCase {

    public FitbitDataUseCase() {}

    public BarData getFitbitStepsBarData(List<FitbitStepsData> data) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (FitbitStepsData fitbitStepsData : data) {
            entries.add(new BarEntry((float) fitbitStepsData.getDate().getDayOfMonth(), Float.parseFloat(fitbitStepsData.getStepsValue())));
        }

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Wykres label");
        barDataSet.setColors(colors);
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarData barData = new BarData(barDataSet);
        barData.setDrawValues(true);
        return barData;
    }

    public BarData getFitbitSpO2BarData(List<FitbitSpO2Data> data) {
        ArrayList<BarEntry> entries = new ArrayList<>();

        for (FitbitSpO2Data fitbitSpO2Data : data) {
            entries.add(new BarEntry((float) fitbitSpO2Data.getDate().getDayOfMonth(), Float.parseFloat(fitbitSpO2Data.getSpO2Value())));
        }

        ArrayList<Integer> colors = new ArrayList<>();
        for (int color : ColorTemplate.MATERIAL_COLORS) {
            colors.add(color);
        }

        BarDataSet barDataSet = new BarDataSet(entries, "Wykres label");
        barDataSet.setColors(colors);
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarData barData = new BarData(barDataSet);
        barData.setDrawValues(true);
        return barData;
    }
}
