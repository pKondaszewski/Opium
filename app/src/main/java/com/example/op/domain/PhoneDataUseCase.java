package com.example.op.domain;

import android.content.Context;
import android.graphics.Color;

import com.example.op.R;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;
import java.util.Objects;

public class PhoneDataUseCase {
    public PieData getPhoneQuestionAnswersPieData(Context context, Integer numberOfCorrectAnswers, Integer numberOfIncorrectAnswers) {
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();

        if (Objects.nonNull(numberOfCorrectAnswers) && numberOfCorrectAnswers != 0) {
            entries.add(new PieEntry(numberOfCorrectAnswers, context.getString(R.string.correct_chart_legend_label)));
            colors.add(Color.GREEN);
        }
        if (Objects.nonNull(numberOfIncorrectAnswers) && numberOfIncorrectAnswers != 0) {
            entries.add(new PieEntry(numberOfIncorrectAnswers, context.getString(R.string.incorrect_chart_legend_label)));
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
