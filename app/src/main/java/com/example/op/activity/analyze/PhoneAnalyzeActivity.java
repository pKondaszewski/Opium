package com.example.op.activity.analyze;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.database.AppDatabase;
import com.example.database.HomeAddress;
import com.example.database.dao.PhoneLocalizationDao;
import com.example.database.dao.PhoneMovementDao;
import com.example.op.R;
import com.example.op.activity.extra.GlobalSetupAppCompatActivity;
import com.example.op.domain.PhoneDataUseCase;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class PhoneAnalyzeActivity extends GlobalSetupAppCompatActivity {
    private final LocalDate now = LocalDate.now();
    private AppDatabase database;
    private PieChart pieChart;
    private PhoneDataUseCase phoneDataUseCase;
    private DateTimeFormatter formatter;
    private TextView lastDailyQuestionValueTv, lastDailyQuestionAnswerValueTv, lastDailyQuestionTimeOfAnswerValueTv,
            dailyPhoneMovementValueTv, lastPhoneMovementValueTv, phoneMovementAverageTodayValueTv,
            phoneMovementAverageAllValueTv, phoneMovementCountValueTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_phone);

        database = AppDatabase.getDatabaseInstance(this);
        formatter = DateTimeFormatter.ofPattern("HH:mm");

        LinearLayout todayPhoneMovementLl = findViewById(R.id.linear_layout_phone_movement_today);
        LinearLayout allTimePhoneMovementLl = findViewById(R.id.linear_layout_phone_movement_all_time);

        lastDailyQuestionValueTv = findViewById(R.id.text_view_last_daily_question_value);
        lastDailyQuestionAnswerValueTv = findViewById(R.id.text_view_last_daily_question_answer_value);
        lastDailyQuestionTimeOfAnswerValueTv = findViewById(R.id.text_view_last_daily_question_time_of_answer_value);
        dailyPhoneMovementValueTv = findViewById(R.id.text_view_daily_phone_movement_value);
        lastPhoneMovementValueTv = findViewById(R.id.text_view_last_phone_movement_value);
        phoneMovementCountValueTv = findViewById(R.id.text_view_phone_movement_count_value);
        phoneMovementAverageTodayValueTv = findViewById(R.id.text_view_phone_movement_average_today_value);
        phoneMovementAverageAllValueTv = findViewById(R.id.text_view_phone_movement_average_all_value);

        pieChart = findViewById(R.id.pie_chart);
        phoneDataUseCase = new PhoneDataUseCase();

        PhoneLocalizationDao phoneLocalizationDao = database.phoneLocalizationDao();
        var mostFrequentLocationAndCountToday = phoneLocalizationDao.getMostFrequentLocationAndCountToday(now);
        var mostFrequentLocationAndCountAllTime = phoneLocalizationDao.getMostFrequentLocationAndCountAllTime();

        generateDailyQuestionData();
        generateMovementTable();
        generateLocationsTable(mostFrequentLocationAndCountToday, todayPhoneMovementLl);
        generateLocationsTable(mostFrequentLocationAndCountAllTime, allTimePhoneMovementLl);
    }

    private void setH3Style(TextView textView) {
        textView.setTextSize(16);
        textView.setTextColor(Color.rgb(125, 125, 125));
        textView.setPadding(14, 3, 14, 3);
    }

    private void generateDailyQuestionData() {
        generateDailyQuestionChart();
        database.dailyQuestionAnswerDao().getNewestId().ifPresent(answerDto -> {
            String textQuestion = database.dailyQuestionDao().getTextQuestionById(answerDto.getDailyQuestionId()).get();
            lastDailyQuestionValueTv.setText(textQuestion);
            lastDailyQuestionAnswerValueTv.setText(answerDto.getUserAnswer() == null ? "" : answerDto.getUserAnswer().toString());
            lastDailyQuestionTimeOfAnswerValueTv.setText(formatter.format(answerDto.getTimeOfAnswer()));
        });
        database.phoneMovementDao().getNewestPhoneMovementByDate(now)
                .ifPresent(localTime -> lastPhoneMovementValueTv.setText(formatter.format(localTime)));

    }

    private void generateDailyQuestionChart() {
        Integer correctAnswersCount = database.dailyQuestionAnswerDao().getAllCorrectCount().get();
        Integer incorrectAnswersCount = database.dailyQuestionAnswerDao().getAllIncorrectCount().get();
        int allAnswersCount = correctAnswersCount + incorrectAnswersCount;
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
        PhoneMovementDao phoneMovementDao = database.phoneMovementDao();
        List<LocalTime> allByDate = phoneMovementDao.getAllTimeByDate(now);
        List<LocalTime> allMovements = phoneMovementDao.getAllTime();
        int movementsByDateCount = allByDate.size();
        int allMovementsSize = allMovements.size();

        dailyPhoneMovementValueTv.setText(movementsByDateCount == 0 ? "" : String.format("%d", movementsByDateCount));
        phoneMovementCountValueTv.setText(allMovementsSize == 0 ? "" : String.valueOf(allMovementsSize));

        allByDate.stream().mapToDouble(LocalTime::toNanoOfDay).average().ifPresent(value -> {
            LocalTime localTime = LocalTime.ofNanoOfDay((long) value);
            phoneMovementAverageTodayValueTv.setText(formatter.format(localTime));
        });

        if (!allMovements.isEmpty()) {
            LocalTime averageTime = allMovements.get(allMovementsSize/2);
            phoneMovementAverageAllValueTv.setText(averageTime.format(formatter));
        }
    }

    private void generateLocationsTable(Map<HomeAddress, String> dataMap, LinearLayout dataLayout) {
        if (!dataMap.isEmpty()) {
            TableLayout tableLayout = new TableLayout(this);
            tableLayout.setColumnStretchable(1, true);
            dataMap.forEach((k,v) -> {
                TableRow tableRow = new TableRow(this);
                TextView textView = new TextView(this);
                TextView textView2 = new TextView(this);

                textView.setText(k.toSimpleString());
                setH3Style(textView);
                textView2.setText(v);
                textView2.setGravity(Gravity.END);
                setH3Style(textView2);

                tableRow.addView(textView);
                tableRow.addView(textView2);
                tableLayout.addView(tableRow);
            });
            dataLayout.addView(tableLayout);
        }
    }
}
