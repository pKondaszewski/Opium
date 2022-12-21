package com.example.op.fragment.report;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.database.AppDatabase;
import com.example.database.entity.ControlTextQuestion;
import com.example.database.entity.ControlTextUserAnswer;
import com.example.database.entity.DailyFeelings;
import com.example.database.entity.ExpertSystemResult;
import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;
import com.example.database.entity.PhoneLocalization;
import com.example.database.entity.PhoneMovement;
import com.example.op.R;
import com.example.op.utils.FitbitUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DetailedReportFragment extends GeneralReportFragment {

    private AppDatabase database;
    private CardView fitbitDataCv;
    private SharedPreferences sharPref;
    private TextView dateValueTextView, nameValueTextView, birthdateValueTextView, fitbitStepsValueTv,
            fitbitSpO2ValueTv, moodDailyFeelingsValueTv, ailmentsDailyFeelingsValueTv, noteDailyFeelingsValueTv,
            phoneMovementValueTv, phoneLocalizationValueTv, controlQuestionContentTv,
            controlQuestionAnswerTv, controlQuestionResultTv, analyzeResultValueTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detailed_report, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LocalDate presentDate = LocalDate.now();
        database = AppDatabase.getDatabaseInstance(getContext());
        sharPref = getContext().getSharedPreferences(getString(com.example.database.R.string.opium_preferences), Context.MODE_PRIVATE);

        dateValueTextView = view.findViewById(R.id.text_view_date);
        nameValueTextView = view.findViewById(R.id.text_view_name_value);
        birthdateValueTextView = view.findViewById(R.id.text_view_birthdate_value);
        moodDailyFeelingsValueTv = view.findViewById(R.id.text_view_mood_daily_feelings_value);
        ailmentsDailyFeelingsValueTv = view.findViewById(R.id.text_view_ailments_daily_feelings_value);
        noteDailyFeelingsValueTv = view.findViewById(R.id.text_view_note_daily_feelings_value);

        controlQuestionContentTv = view.findViewById(R.id.text_view_control_question_content_value);
        controlQuestionAnswerTv = view.findViewById(R.id.text_view_control_question_answer_value);
        controlQuestionResultTv = view.findViewById(R.id.text_view_control_question_result_value);

        phoneMovementValueTv = view.findViewById(R.id.text_view_phone_movement_value);
        phoneLocalizationValueTv = view.findViewById(R.id.text_view_phone_localization_value);

        fitbitDataCv = view.findViewById(R.id.fitbitDataCardView);
        fitbitStepsValueTv = view.findViewById(R.id.text_view_fitbit_steps_value);
        fitbitSpO2ValueTv = view.findViewById(R.id.text_view_fitbit_spo2_value);

        analyzeResultValueTv = view.findViewById(R.id.text_view_analyze_result_value);

        setupData(presentDate);
    }


    private void setupData(LocalDate presentDate) {
        try {
            setupPersonalData(presentDate, List.of(dateValueTextView, nameValueTextView, birthdateValueTextView), database);
            setupDailyFeelingsData(presentDate);
            setupControlQuestion(presentDate);
            setupPhoneMovementData(presentDate);
            setupPhoneLocalizationData();
            if (isFitbitEnable()) {
                setFitbitDataVisible();
                setupFitbitData(presentDate);
            }
            analyzeResultValueTv.setText(setupResults(presentDate, sharPref));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    private boolean isFitbitEnable() {
        return FitbitUtils.isEnabled(sharPref, getString(com.example.database.R.string.fitbit_switch_state));
    }

    private void setFitbitDataVisible() {
        fitbitDataCv.setVisibility(View.VISIBLE);
    }

    private void setupDailyFeelingsData(LocalDate presentDate) {
        DailyFeelings dailyFeelings = database.dailyFeelingsDao().getByDate(presentDate)
                .orElse(new DailyFeelings());

        moodDailyFeelingsValueTv.setText(dailyFeelings.getMood());
        List<String> ailments = dailyFeelings.getAilments();
        if (ailments != null) {
            ArrayList<String> ailmentsArrayList = new ArrayList<>(ailments);
            int otherIndex = ailmentsArrayList.indexOf("other");
            if (otherIndex != -1) {
                String otherValue = String.format("%s: %s", "other", dailyFeelings.getOtherAilments());
                ailmentsArrayList.set(otherIndex, otherValue);
                ailmentsDailyFeelingsValueTv.setText(String.join(", ", ailmentsArrayList));
            } else {
                ailmentsDailyFeelingsValueTv.setText(String.join(", ", ailments));
            }
            noteDailyFeelingsValueTv.setText(dailyFeelings.getNote());
        }
    }

    private void setupControlQuestion(LocalDate presentDate) {
        Optional<ControlTextUserAnswer> controlTextUserAnswer = database.controlTextUserAnswerDao()
                .getNewestByDate(presentDate);
        if (controlTextUserAnswer.isPresent()) {
            ControlTextUserAnswer userAnswer = controlTextUserAnswer.get();
            Integer questionId = userAnswer.getControlTextQuestionId();
            ControlTextQuestion controlQuestion = database.controlTextQuestionDao().
                    getById(questionId).orElse(new ControlTextQuestion());

            controlQuestionContentTv.setText(controlQuestion.getTextQuestion());
            controlQuestionAnswerTv.setText(String.valueOf(userAnswer.getUserAnswer()));
            controlQuestionResultTv.setText(String.valueOf(controlQuestion.getCorrectAnswer()));
        } else {
            controlQuestionContentTv.setText("");
            controlQuestionAnswerTv.setText("");
            controlQuestionResultTv.setText("");
        }
    }

    private void setupPhoneMovementData(LocalDate presentDate) {
        List<LocalTime> averageTimeOfMovementsByDate = database.phoneMovementDao().getAverageTimeOfMovementsByDate(presentDate);
        double average = averageTimeOfMovementsByDate.stream().mapToInt(LocalTime::toSecondOfDay).average().getAsDouble();
        LocalTime localTime = LocalTime.ofSecondOfDay((long) average);

        phoneMovementValueTv.setText(localTime.toString());
    }

    private void setupPhoneLocalizationData() throws IOException {
        PhoneLocalization phoneLocalization = database.phoneLocalizationDao().getNewestLocation().orElse(
                new PhoneLocalization());
        Geocoder geocoder = new Geocoder(getContext());
        String postalAddress = null;
        for (Address address : geocoder.getFromLocation(phoneLocalization.getLatitude(), phoneLocalization.getLongitude(), 1)) {
            postalAddress = address.getAddressLine(0);
        }
        phoneLocalizationValueTv.setText(postalAddress);
    }

    private void setupFitbitData(LocalDate presentDate) {
        FitbitStepsData fitbitStepsData = database.fitbitStepsDataDao().getNewestFitbitStepsDataByDate(presentDate)
                .orElse(new FitbitStepsData());
        FitbitSpO2Data fitbitSpO2Data = database.fitbitSpO2DataDao().getNewestFitbitSpO2DataByDate(presentDate)
                .orElse(new FitbitSpO2Data());

        fitbitStepsValueTv.setText(fitbitStepsData.getStepsValue());
        fitbitSpO2ValueTv.setText(fitbitSpO2Data.getSpO2Value());
    }
}
