package com.example.op.domain;

import android.graphics.Color;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.DefaultAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Map;

public class PhoneDataUseCase {

    public PhoneDataUseCase() {}

    public PieData getPhoneQuestionAnswersPieData(Integer numberOfCorrectAnswers, Integer numberOfIncorrectAnswers) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        if (numberOfCorrectAnswers != 0) {
            entries.add(new PieEntry(numberOfCorrectAnswers, "Correct"));
            colors.add(Color.GREEN);
        }
        if (numberOfIncorrectAnswers != 0) {
            entries.add(new PieEntry(numberOfIncorrectAnswers, "Incorrect"));
            colors.add(Color.RED);
        }

        PieDataSet pieDataSet = new PieDataSet(entries, null);
        pieDataSet.setColors(colors);
        pieDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        PieData pieData = new PieData();
        pieData.setDrawValues(true);
        pieData.setDataSet(pieDataSet);
        return pieData;
    }

    public BarData getPhoneMovementActivityBarData(Map<LocalDate, Integer> movementsCountByEveryDate) {
        ArrayList<BarEntry> entries = new ArrayList<>();
        movementsCountByEveryDate.forEach((k, v) ->
                entries.add(new BarEntry((float) k.getDayOfMonth(), v.floatValue()))
        );

        BarDataSet barDataSet = new BarDataSet(entries, "Wykres label");
        barDataSet.setColor(ColorTemplate.rgb("3587A4"));
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        BarData barData = new BarData(barDataSet);
        barData.setDrawValues(false);
        barData.setValueFormatter(new DefaultAxisValueFormatter(0));
        return barData;
    }
}
