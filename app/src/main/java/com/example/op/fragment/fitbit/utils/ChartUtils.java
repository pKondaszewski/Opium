package com.example.op.fragment.fitbit.utils;

import android.content.Context;

import com.example.op.R;
import com.example.op.fragment.fitbit.marker.DaysMarkerView;
import com.example.op.fragment.fitbit.marker.MonthsMarkerView;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.time.LocalDate;
import java.time.Year;
import java.util.ArrayList;

public class ChartUtils {

    public static void generateBarChart(BarChart barChart, Integer gran, Context context) {
        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getLegend().setEnabled(false);
        barChart.setVisibleXRangeMaximum(25);
        barChart.moveViewTo(barChart.getXChartMax(), 0, YAxis.AxisDependency.RIGHT);


        barChart.getAxisLeft().setTextSize(13);
        barChart.getAxisLeft().setGranularity(1);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setTextSize(13);
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        if (gran != 1) {
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    value = Math.max(value, 1);
                    LocalDate date = Year.of(LocalDate.now().getYear()).atDay((int) value);
                    return date.getDayOfMonth() + "." + date.getMonth().getValue();
                }
            });
            xAxis.setGranularity(Math.max(gran/4, 1));
            DaysMarkerView mv = new DaysMarkerView(context, R.layout.custom_marker_view_layout);
            barChart.setMarker(mv);
        } else {
            xAxis.setValueFormatter(new ValueFormatter() {
                @Override
                public String getFormattedValue(float value) {
                    value = Math.max(value, 1);
                    int date = Year.of(LocalDate.now().getYear()).atDay((int) value).getDayOfYear();
                    return String.valueOf(date);
                }
            });
            xAxis.setGranularity(gran);
            MonthsMarkerView mv = new MonthsMarkerView(context, R.layout.custom_marker_view_layout);
            barChart.setMarker(mv);
        }
        barChart.invalidate();
    }

    public static BarData generateBarData(ArrayList<BarEntry> entries) {
        BarDataSet barDataSet = new BarDataSet(entries, "Wykres label");
        barDataSet.setColor(ColorTemplate.rgb("3587A4"));
        barDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        BarData barData = new BarData(barDataSet);
        barData.setDrawValues(false);
        return barData;
    }
}
