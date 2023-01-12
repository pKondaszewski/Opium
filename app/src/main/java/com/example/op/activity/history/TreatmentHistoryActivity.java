package com.example.op.activity.history;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.example.database.dto.DailyFeelingsDto;
import com.example.database.entity.DailyFeelings;
import com.example.database.entity.DailyQuestion;
import com.example.database.entity.DailyQuestionAnswer;
import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;
import com.example.op.R;
import com.example.op.activity.extra.GlobalSetupAppCompatActivity;
import com.example.op.activity.user.DailyFeelingsActivity;
import com.example.op.utils.FitbitUtils;
import com.example.op.utils.JsonManipulator;
import com.example.op.utils.LocalDateUtils;
import com.example.op.utils.Translation;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class TreatmentHistoryActivity extends GlobalSetupAppCompatActivity implements CalendarView.OnDateChangeListener, MenuItem.OnMenuItemClickListener, CompactCalendarView.CompactCalendarViewListener {
    private ActivityResultLauncher<Intent> startActivityForResult;
    private AppDatabase database;
    private CompactCalendarView calendarView;
    private Date presentDate;
    private LocalDate pickedDate;
    private TextView calendarMonthTv, moodDailyFeelingsValueTv, ailmentsDailyFeelingsValueTv, noteDailyFeelingsValueTv,
            dailyQuestionContentTv, dailyQuestionAnswerTv, dailyQuestionResultTv,
            fitbitDataLabelTv, fitbitStepsLabelTv, fitbitStepsValueTv, fitbitSp02LabelTv, fitbitSp02ValueTv;
    private Translation translation;
    private SharedPreferences sharPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment_history);
        sharPref = getSharedPreferences(getString(com.example.database.R.string.opium_preferences), Context.MODE_PRIVATE);

        database = AppDatabase.getDatabaseInstance(this);
        translation = new Translation(this);
        presentDate = new Date();

        calendarMonthTv = findViewById(R.id.calendar_month_text_view);

        calendarView = findViewById(R.id.calendar_view);
        calendarView.setListener(this);

        moodDailyFeelingsValueTv = findViewById(R.id.text_view_mood_daily_feelings_value);
        ailmentsDailyFeelingsValueTv = findViewById(R.id.text_view_ailments_daily_feelings_value);
        noteDailyFeelingsValueTv = findViewById(R.id.text_view_note_daily_feelings_value);

        dailyQuestionContentTv = findViewById(R.id.text_view_daily_question_content_value);
        dailyQuestionAnswerTv = findViewById(R.id.text_view_daily_question_answer_value);
        dailyQuestionResultTv = findViewById(R.id.text_view_daily_question_result_value);

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
                        long pickedDateToMillis = intent.getLongExtra(getString(R.string.picked_date_to_millis), 0L);
                        Date pickedDate = new Date(pickedDateToMillis);
                        onMonthScroll(pickedDate);
                        onDayClick(pickedDate);
                        calendarView.invalidate();
                    }});
        onMonthScroll(presentDate);
        onDayClick(presentDate);
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

        Optional<DailyQuestionAnswer> dailyQuestionAnswer = database.dailyQuestionAnswerDao()
                .getNewestByDate(pickedDate);
        if (dailyQuestionAnswer.isPresent()) {
            DailyQuestionAnswer userAnswer = dailyQuestionAnswer.get();
            Integer questionId = userAnswer.getDailyQuestionId();
            DailyQuestion dailyQuestion = database.dailyQuestionDao().getById(questionId)
                    .orElse(new DailyQuestion());
            outputDailyQuestion(dailyQuestion, userAnswer);
        } else {
            dailyQuestionContentTv.setText("");
            dailyQuestionAnswerTv.setText("");
            dailyQuestionResultTv.setText("");
        }
        if (isFitbitEnable()) {
            setupFitbitData(pickedDate);
        }
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.showCurrentDateMenuItem) {
            calendarView.setCurrentDate(presentDate);
            onDayClick(presentDate);
            onMonthScroll(presentDate);
        } else if (itemId == R.id.editPickedDateMenuItem) {
            if (!pickedDate.isAfter(dateToLocalDate(presentDate))) {
                DailyFeelings dailyFeelings = database.dailyFeelingsDao().getByDate(pickedDate)
                        .orElse(new DailyFeelings(pickedDate));
                String dailyFeelingsString = JsonManipulator.stringifyDailyFeelings(dailyFeelings);
                Intent intent = new Intent(this, DailyFeelingsActivity.class);
                intent.putExtra(getString(com.example.database.R.string.daily_feelings_as_json), dailyFeelingsString);
                intent.setAction(Intent.ACTION_ASSIST);
                startActivityForResult.launch(intent);
            } else {
                Toast.makeText(this, getString(R.string.unable_change_future_date_toast_label), Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    private boolean isFitbitEnable() {
        return FitbitUtils.isEnabled(sharPref, getString(com.example.database.R.string.fitbit_switch_state));
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
        moodDailyFeelingsValueTv.setText(translation.translateMood(dailyFeelings.getMood()));
        List<String> dailyFeelingsAilments = dailyFeelings.getAilments();
        if (dailyFeelingsAilments == null || dailyFeelingsAilments.get(0).equals("")) {
            ailmentsDailyFeelingsValueTv.setText("");
        } else {
            ArrayList<String> dailyFeelingsWithOtherAilments = new ArrayList<>(dailyFeelingsAilments);
            if (dailyFeelingsWithOtherAilments.contains("other")) {
                String otherAilments = dailyFeelings.getOtherAilments();
                String otherValue = "other: " + otherAilments;
                int otherIndex = dailyFeelingsWithOtherAilments.indexOf("other");
                dailyFeelingsWithOtherAilments.set(otherIndex, otherValue);
                List<String> translatedAilments = translation.translateAilments(dailyFeelingsWithOtherAilments);
                ailmentsDailyFeelingsValueTv.setText(String.join(", ", translatedAilments));
            } else {
                List<String> translatedAilments = translation.translateAilments(dailyFeelingsWithOtherAilments);
                ailmentsDailyFeelingsValueTv.setText(String.join(", ", translatedAilments));
            }
        }
        noteDailyFeelingsValueTv.setText(dailyFeelings.getNote());
    }

    private void outputDailyQuestion(DailyQuestion question, DailyQuestionAnswer answer) {
        dailyQuestionContentTv.setText(question.getTextQuestion());
        dailyQuestionAnswerTv.setText(String.valueOf(answer.getUserAnswer()));
        dailyQuestionResultTv.setText(String.valueOf(question.getCorrectAnswer()));
    }

    @Override
    public void onDayClick(Date dateClicked) {
        pickedDate = dateToLocalDate(dateClicked);

        DailyFeelings dailyFeelings = database.dailyFeelingsDao().getByDate(pickedDate).orElse(new DailyFeelings());
        outputDailyFeelings(dailyFeelings);

        Optional<DailyQuestionAnswer> dailyQuestionAnswer = database.dailyQuestionAnswerDao()
                .getNewestByDate(pickedDate);
        if (dailyQuestionAnswer.isPresent()) {
            DailyQuestionAnswer userAnswer = dailyQuestionAnswer.get();
            Integer questionId = userAnswer.getDailyQuestionId();
            DailyQuestion dailyQuestion = database.dailyQuestionDao().getById(questionId)
                    .orElse(new DailyQuestion());
            outputDailyQuestion(dailyQuestion, userAnswer);
        } else {
            dailyQuestionContentTv.setText("");
            dailyQuestionAnswerTv.setText("");
            dailyQuestionResultTv.setText("");
        }
        if (isFitbitEnable()) {
            setupFitbitData(pickedDate);
        }
    }

    private LocalDate dateToLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private int moodColor(String mood) {
        switch (mood) {
            case "tragic": return Color.RED;
            case "bad": return Color.rgb(255, 165, 0);
            case "ok": return Color.YELLOW;
            case "good": return Color.rgb(144,238,144);
            case "great": return Color.GREEN;
            default: return Color.WHITE;
        }
    }

    @Override
    public void onMonthScroll(Date firstDayOfNewMonth) {
        int monthValue = firstDayOfNewMonth.getMonth() + 1;
        String month = Month.of(monthValue).getDisplayName(TextStyle.SHORT, Locale.getDefault());
        calendarMonthTv.setText(String.format(Locale.getDefault(), "%s %d", StringUtils.capitalize(month), firstDayOfNewMonth.getYear() + 1900));
        calendarView.removeAllEvents();

        String monthAsString = LocalDateUtils.monthFromInt(monthValue);
        String yearAsString = LocalDateUtils.yearFromDate(firstDayOfNewMonth);

        setupDailyFeelingsEvents(monthAsString, yearAsString);
        setupDailyQuestionsEvents(monthAsString, yearAsString);

        String isFitbitEnabled = sharPref.getString(getString(com.example.database.R.string.fitbit_switch_state), "false");
        if (Boolean.parseBoolean(isFitbitEnabled)) {
            setupFitbitStepsEvents(monthAsString);
            setupFitbitSpO2Events(monthAsString);
        }
    }

    private void setupDailyFeelingsEvents(String month, String year) {
        List<DailyFeelingsDto> allFeelingDatesFromMonth = database.dailyFeelingsDao().getAllFromMonthAndYear(month, year);
        allFeelingDatesFromMonth.forEach(dto -> calendarView.addEvent(new Event(moodColor(dto.getMood()),
                Date.from(dto.getDateOfDailyFeelings().atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()
        )));
    }

    private void setupDailyQuestionsEvents(String month, String year) {
        List<LocalDate> allQuestionDatesFromMonth = database.dailyQuestionAnswerDao().getAllFromMonth(month, year);
        allQuestionDatesFromMonth.forEach(localDate -> calendarView.addEvent(new Event(Color.WHITE,
                Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()
        )));
    }

    private void setupFitbitStepsEvents(String month) {
        List<LocalDate> allFitbitStepsFromMonth = database.fitbitStepsDataDao().getAllFromMonth(month);
        allFitbitStepsFromMonth.forEach(localDate -> calendarView.addEvent(new Event(Color.CYAN,
                Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()
        )));
    }

    private void setupFitbitSpO2Events(String month) {
        List<LocalDate> allFitbitSpO2FromMonth = database.fitbitSpO2DataDao().getAllFromMonth(month);
        allFitbitSpO2FromMonth.forEach(localDate -> calendarView.addEvent(new Event(Color.CYAN,
                Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()).getTime()
        )));
    }
}
