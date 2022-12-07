package com.example.op.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.database.AppDatabase;
import com.example.op.R;
import com.example.op.domain.PhoneDataUseCase;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.time.LocalDate;
import java.util.Map;

public class PhoneAnalyzeActivity extends AppCompatActivity {

    private AppDatabase database;
    private PieChart pieChart;
    private BarChart barChart2;
    private PhoneDataUseCase phoneDataUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_phone);

        database = AppDatabase.getDatabaseInstance(this);
        LinearLayout phoneMovementLinearLayout = findViewById(R.id.phoneMovementLinearLayout);

        pieChart = findViewById(R.id.pieChart);
        barChart2 = findViewById(R.id.barChart2);
        phoneDataUseCase = new PhoneDataUseCase();

        generateCharts();

        var mostFrequentLocationAndCount = database.phoneLocalizationDao().getMostFrequentLocationAndCount();
        if (!mostFrequentLocationAndCount.isEmpty()) {
            TableLayout tableLayout = new TableLayout(this);
            tableLayout.setColumnStretchable(1, true);

            TableRow titleTableRow = new TableRow(this);
            TextView titleTextView = new TextView(this);
            titleTextView.setText("Najczęściej odnotowane lokalizacje telefonu");
            titleTableRow.addView(titleTextView);
            tableLayout.addView(titleTableRow);

            mostFrequentLocationAndCount.forEach((k,v) -> {
                TableRow tableRow = new TableRow(this);
                TextView textView = new TextView(this);
                TextView textView2 = new TextView(this);

                textView.setText(k.toSimpleString());
                textView.setPadding(25, 25, 25, 25);
                tableRow.addView(textView);
                textView2.setText(v);
                textView2.setPadding(25, 25, 25, 25);
                textView2.setGravity(Gravity.END);
                tableRow.addView(textView2);

                tableLayout.addView(tableRow);
            });
            phoneMovementLinearLayout.addView(tableLayout);
        }
    }

    private void generateCharts() {
        Integer correctAnswersCount = database.controlTextUserAnswerDao().getAllCorrectCount().get();
        Integer incorrectAnswersCount = database.controlTextUserAnswerDao().getAllIncorrectCount().get();
        Integer allAnswersCount = correctAnswersCount + incorrectAnswersCount;
        PieData pieData = phoneDataUseCase.getPhoneQuestionAnswersPieData(correctAnswersCount, incorrectAnswersCount);

        pieData.setValueTextSize(20f);
        pieData.setValueFormatter(new PercentFormatter(pieChart));
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(20f);

        pieChart.getDescription().setEnabled(false);
        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setWordWrapEnabled(true);

        pieChart.setHoleColor(255);
        pieChart.setNoDataText("Brak danych");
        pieChart.setCenterText(String.format("Ilość wszyskich odpowiedzi: %d", allAnswersCount));
        pieChart.setData(pieData);
        pieChart.setEntryLabelTextSize(0);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EaseInOutQuad);

        final Map<LocalDate, Integer> movementsCountByEveryDate = database.phoneMovementDao().getMovementsCountByEveryDate(7);

        BarData phoneMovementActivityBarData;
        if (movementsCountByEveryDate.isEmpty()) {
            phoneMovementActivityBarData = null;
        } else {
            phoneMovementActivityBarData = phoneDataUseCase.getPhoneMovementActivityBarData(movementsCountByEveryDate);
        }

        barChart2.setData(phoneMovementActivityBarData);

        barChart2.getDescription().setEnabled(false);
        barChart2.getAxisRight().setEnabled(false);
        barChart2.getAxisLeft().setGranularity(1);
        barChart2.setFocusableInTouchMode(false);
        barChart2.setFocusable(false);
        barChart2.getAxisLeft().setAxisMaximum(movementsCountByEveryDate.size());
//        barChart2.getAxisLeft().setEnabled(false);
        barChart2.getLegend().setEnabled(false);

        barChart2.getXAxis().setTextSize(25);

        barChart2.getXAxis().setDrawGridLines(false);
        barChart2.getXAxis().setAvoidFirstLastClipping(true);
        barChart2.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart2.getXAxis().setGranularity(1);

        barChart2.setNoDataText("Brak danych");
        barChart2.invalidate();
    }
}
