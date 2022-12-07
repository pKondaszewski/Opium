package com.example.op.activity.history;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.database.AppDatabase;
import com.example.database.entity.ControlTextQuestion;
import com.example.database.entity.ControlTextUserAnswer;
import com.example.database.entity.DailyFeelings;
import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;
import com.example.op.R;
import com.example.op.activity.user.DailyFeelingsActivity;
import com.example.op.utils.FitbitUtils;
import com.example.op.utils.JsonManipulator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public class TreatmentHistoryActivity extends AppCompatActivity implements CalendarView.OnDateChangeListener, MenuItem.OnMenuItemClickListener {

    private ActivityResultLauncher<Intent> startActivityForResult;
    private AppDatabase database;
    private Calendar calendar;
    private CalendarView calendarView;
    private LocalDate pickedDate, localDate;
    private TextView moodDailyFeelingsValueTv, ailmentsDailyFeelingsValueTv, noteDailyFeelingsValueTv,
            dailyFeelingsQuestionContentTv, dailyFeelingsQuestionAnswerTv, dailyFeelingsQuestionResultTv,
            fitbitDataLabelTv, fitbitStepsLabelTv, fitbitStepsValueTv, fitbitSp02LabelTv, fitbitSp02ValueTv;
    private SharedPreferences sharPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment_history);
        sharPref = getSharedPreferences(getString(R.string.opium_preferences), Context.MODE_PRIVATE);

        database = AppDatabase.getDatabaseInstance(this);
        calendar = Calendar.getInstance();
        localDate = LocalDate.now();

        calendarView = findViewById(R.id.calendar_view);
        calendarView.setOnDateChangeListener(this);

        moodDailyFeelingsValueTv = findViewById(R.id.text_view_mood_daily_feelings_value);
        ailmentsDailyFeelingsValueTv = findViewById(R.id.text_view_ailments_daily_feelings_value);
        noteDailyFeelingsValueTv = findViewById(R.id.text_view_note_daily_feelings_value);

        dailyFeelingsQuestionContentTv = findViewById(R.id.text_view_control_question_content_value);
        dailyFeelingsQuestionAnswerTv = findViewById(R.id.text_view_control_question_answer_value);
        dailyFeelingsQuestionResultTv = findViewById(R.id.text_view_control_question_result_value);

        fitbitDataLabelTv = findViewById(R.id.text_view_fitbit_data_label);
        fitbitStepsLabelTv = findViewById(R.id.text_view_fitbit_steps_label);
        fitbitStepsValueTv = findViewById(R.id.text_view_fitbit_steps_value);
        fitbitSp02LabelTv = findViewById(R.id.text_view_fitbit_spo2_label);
        fitbitSp02ValueTv = findViewById(R.id.text_view_fitbit_spo2_value);
        if (isFitbitEnable()) {
            setFitbitDataVisible();
        }
        startActivityForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == AppCompatActivity.RESULT_OK) {
                        Intent intent = result.getData();
                        long pickedDateAsEpochDay = intent.getLongExtra(getString(R.string.picked_date_as_epoch_day), 0L);
                        LocalDate localDate = LocalDate.ofEpochDay(pickedDateAsEpochDay);
                        onSelectedDayChange(calendarView, localDate.getYear(), localDate.getMonthValue()-1,
                                localDate.getDayOfMonth());
                    }
                }
        );
        onSelectedDayChange(calendarView, localDate.getYear(), localDate.getMonthValue()-1, localDate.getDayOfMonth());
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_treatment_history, menu);
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setOnMenuItemClickListener(this);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
        pickedDate = LocalDate.of(year, month+1, dayOfMonth);

        DailyFeelings dailyFeelings = database.dailyFeelingsDao().getByDate(pickedDate).orElse(new DailyFeelings());
        outputDailyFeelings(dailyFeelings);

        Optional<ControlTextUserAnswer> controlTextUserAnswer = database.controlTextUserAnswerDao()
                .getByDate(pickedDate);
        if (controlTextUserAnswer.isPresent()) {
            ControlTextUserAnswer userAnswer = controlTextUserAnswer.get();
            Integer questionId = userAnswer.getControlTextQuestionId();
            ControlTextQuestion controlQuestion = database.controlTextQuestionDao().getById(questionId)
                    .orElse(new ControlTextQuestion());
            outputControlQuestion(controlQuestion, userAnswer);
        } else {
            dailyFeelingsQuestionContentTv.setText("");
            dailyFeelingsQuestionAnswerTv.setText("");
            dailyFeelingsQuestionResultTv.setText("");
        }
        if (isFitbitEnable()) {
            setupFitbitData(pickedDate);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.showCurrentDateMenuItem) {
            calendarView.setDate(calendar.toInstant().toEpochMilli(), true, false);
            onSelectedDayChange(calendarView, localDate.getYear(), localDate.getMonthValue()-1, localDate.getDayOfMonth());
        } else if (itemId == R.id.editPickedDateMenuItem) {
            if (!pickedDate.isAfter(localDate)) {
                DailyFeelings dailyFeelings = database.dailyFeelingsDao().getByDate(pickedDate)
                        .orElse(new DailyFeelings(pickedDate));
                String dailyFeelingsString = JsonManipulator.stringifyDailyFeelings(dailyFeelings);
                Intent intent = new Intent(this, DailyFeelingsActivity.class);
                intent.putExtra(getString(R.string.daily_feelings_as_json), dailyFeelingsString);
                intent.setAction(Intent.ACTION_ASSIST);
                startActivityForResult.launch(intent);
            } else {
                Toast.makeText(this, getString(R.string.unable_change_future_date_toast_label), Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    private boolean isFitbitEnable() {
        return FitbitUtils.isEnabled(sharPref, getString(R.string.fitbit_switch_state));
    }

    private void setFitbitDataVisible() {
        fitbitDataLabelTv.setVisibility(View.VISIBLE);
        fitbitStepsLabelTv.setVisibility(View.VISIBLE);
        fitbitStepsValueTv.setVisibility(View.VISIBLE);
        fitbitSp02LabelTv.setVisibility(View.VISIBLE);
        fitbitSp02ValueTv.setVisibility(View.VISIBLE);
    }

    private void setupFitbitData(LocalDate pickedDate) {
        FitbitStepsData fitbitStepsData = database.fitbitStepsDataDao().getNewestFitbitStepsDataByDate(pickedDate)
                .orElse(new FitbitStepsData());
        FitbitSpO2Data fitbitSpO2Data = database.fitbitSpO2DataDao().getNewestFitbitSpO2DataByDate(pickedDate)
                .orElse(new FitbitSpO2Data());
        fitbitStepsValueTv.setText(fitbitStepsData.getStepsValue());
        fitbitSp02ValueTv.setText(fitbitSpO2Data.getSpO2Value());
    }

    private void outputDailyFeelings(DailyFeelings dailyFeelings) {
        moodDailyFeelingsValueTv.setText(dailyFeelings.getMood());
        List<String> dailyFeelingsAilments = dailyFeelings.getAilments();
        if (dailyFeelingsAilments == null) {
            ailmentsDailyFeelingsValueTv.setText("");
        } else {
            ArrayList<String> dailyFeelingsWithOtherAilments = new ArrayList<>(dailyFeelingsAilments);
            if (dailyFeelingsWithOtherAilments.contains("other")) {
                String otherAilments = dailyFeelings.getOtherAilments();
                String otherValue = String.format("%s: %s", getString(R.string.ailments_other_button), otherAilments);
                int otherIndex = dailyFeelingsWithOtherAilments.indexOf("other");
                dailyFeelingsWithOtherAilments.set(otherIndex, otherValue);
                ailmentsDailyFeelingsValueTv.setText(String.join(", ", dailyFeelingsWithOtherAilments));
            } else {
                ailmentsDailyFeelingsValueTv.setText(String.join(", ", dailyFeelingsAilments));
            }
        }
        noteDailyFeelingsValueTv.setText(dailyFeelings.getNote());
    }

    private void outputControlQuestion(ControlTextQuestion question, ControlTextUserAnswer answer) {
        dailyFeelingsQuestionContentTv.setText(question.getTextQuestion());
        dailyFeelingsQuestionAnswerTv.setText(String.valueOf(answer.getUserAnswer()));
        dailyFeelingsQuestionResultTv.setText(String.valueOf(question.getCorrectAnswer()));
    }
}
