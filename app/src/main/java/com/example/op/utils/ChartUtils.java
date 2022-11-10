package com.example.op.utils;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;

public class ChartUtils {

    public static void generateBarChart(BarChart barChart, int granularity) {
        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setEnabled(false);
        barChart.getLegend().setEnabled(false);

        barChart.getXAxis().setDrawGridLines(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM_INSIDE);
        barChart.getXAxis().setGranularity(granularity);

        barChart.setNoDataText("Brak danych");
        barChart.invalidate();
    }
}
