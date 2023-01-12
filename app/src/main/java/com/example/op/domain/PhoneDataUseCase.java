package com.example.op.domain;

import android.graphics.Color;

import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

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
}
