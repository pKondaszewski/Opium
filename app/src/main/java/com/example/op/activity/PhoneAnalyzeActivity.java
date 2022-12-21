package com.example.op.activity;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.database.AppDatabase;
import com.example.database.entity.PhoneMovement;
import com.example.op.R;
import com.example.op.domain.PhoneDataUseCase;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.time.LocalDate;
import java.util.List;

public class PhoneAnalyzeActivity extends AppCompatActivity {

    private LocalDate now = LocalDate.now();
    private AppDatabase database;
    private LinearLayout phoneMovementLl;
    private PieChart pieChart;
    private PhoneDataUseCase phoneDataUseCase;
    private TextView dailyPhoneMovementValueTv, phoneMovementAverageValueTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_phone);

        database = AppDatabase.getDatabaseInstance(this);
        phoneMovementLl = findViewById(R.id.phoneMovementLinearLayout);

        dailyPhoneMovementValueTv = findViewById(R.id.text_view_daily_phone_movement_value);
        phoneMovementAverageValueTv = findViewById(R.id.text_view_phone_movement_average_value);

        pieChart = findViewById(R.id.pie_chart);
        phoneDataUseCase = new PhoneDataUseCase();

        generateChart();
        generateMovementTable();
        generateMostFrequentLocationsTable();
    }

    private void setH2Style(TextView textView) {
        textView.setTextSize(19);
        textView.setTextColor(Color.rgb(0,0,0));
        textView.setPadding(14, 3, 14, 3);
        textView.setPadding(7, 7, 7, 7);
    }

    private void setH3Style(TextView textView) {
        textView.setTextSize(16);
        textView.setTextColor(Color.rgb(125, 125, 125));
        textView.setPadding(14, 3, 14, 3);
    }

    private void generateChart() {
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
        pieChart.setNoDataText(getString(R.string.no_data_text_view));
        pieChart.setCenterText(getString(R.string.amount_of_all_answers_text_view) + allAnswersCount);
        pieChart.setData(pieData);
        pieChart.setEntryLabelTextSize(0);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EaseInOutQuad);
    }

    private void generateMovementTable() {
        List<PhoneMovement> allByDate = database.phoneMovementDao().getAllByDate(now);
        Integer movementsByDateCount = allByDate.size();
        dailyPhoneMovementValueTv.setText(String.format("%d", movementsByDateCount));
        if (allByDate.isEmpty()) {
            phoneMovementAverageValueTv.setText("0");
        } else {
            PhoneMovement phoneMovement = allByDate.get(movementsByDateCount / 2);
            phoneMovementAverageValueTv.setText(phoneMovement.getTimeOfMovement().toString());
        }
    }

    private void generateMostFrequentLocationsTable() {
        var mostFrequentLocationAndCount = database.phoneLocalizationDao().getMostFrequentLocationAndCount();
        if (!mostFrequentLocationAndCount.isEmpty()) {
            TableLayout tableLayout = new TableLayout(this);
            tableLayout.setColumnStretchable(1, true);

            TableRow titleTableRow = new TableRow(this);
            TextView titleTextView = new TextView(this);
            titleTextView.setText(getString(R.string.most_common_localizations_text_view));

            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int screenWidth = size.x;

            titleTextView.setMaxWidth(screenWidth);
            setH2Style(titleTextView);
            titleTableRow.addView(titleTextView);
            tableLayout.addView(titleTableRow);

            mostFrequentLocationAndCount.forEach((k,v) -> {
                TableRow tableRow = new TableRow(this);
                TextView textView = new TextView(this);
                textView.setText(k.toSimpleString() + ": " + v);
                textView.setWidth(screenWidth);
                setH3Style(textView);
                tableRow.addView(textView);
                tableLayout.addView(tableRow);
            });
            phoneMovementLl.addView(tableLayout);
        }
    }
}
