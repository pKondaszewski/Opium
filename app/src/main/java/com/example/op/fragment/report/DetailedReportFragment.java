package com.example.op.fragment.report;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.example.database.AppDatabase;
import com.example.op.R;
import com.example.database.entity.ControlTextQuestion;
import com.example.database.entity.ControlTextUserAnswer;
import com.example.database.entity.DailyFeelings;
import com.example.database.entity.FitbitStepsData;
import com.example.database.entity.PhoneLocalization;
import com.example.database.entity.PhoneMovement;
import com.example.op.utils.Authorization;
import com.example.op.utils.FitbitUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.SneakyThrows;

public class DetailedReportFragment extends GeneralReportFragment {

    private AppDatabase database;
    private CardView fitbitDataCv;
    private SharedPreferences sharPref;
    private TextView fitbitStepsValueTv, moodDailyFeelingsValueTv, ailmentsDailyFeelingsValueTv,
            noteDailyFeelingsValueTv, phoneMovementValueTv, phoneLocalizationValueTv, controlQuestionContentTv,
            controlQuestionAnswerTv, controlQuestionResultTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_detailed_report, parent, false);
    }

    @SneakyThrows
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LocalDate presentDate = LocalDate.now();

        database = AppDatabase.getDatabaseInstance(getContext());

        sharPref = getContext().getSharedPreferences(getString(R.string.opium_preferences), Context.MODE_PRIVATE);

        String clientId = getString(R.string.client_id);
        String clientSecret = getString(R.string.client_secret);
        String scopes = getString(R.string.scopes);
        String redirectUrl = getString(R.string.redirect_url);
        Authorization authorization = new Authorization(clientId, clientSecret, scopes, redirectUrl);

        MenuItem settingsReportMi = view.findViewById(R.id.settingsReportMenuItem);

        TextView dateValueTextView = view.findViewById(R.id.text_view_date_value);
        TextView nameValueTextView = view.findViewById(R.id.text_view_name_value);
        TextView birthdateValueTextView = view.findViewById(R.id.text_view_birthdate_value);
        moodDailyFeelingsValueTv = view.findViewById(R.id.text_view_mood_daily_feelings_value);
        ailmentsDailyFeelingsValueTv = view.findViewById(R.id.text_view_ailments_daily_feelings_value);
        noteDailyFeelingsValueTv = view.findViewById(R.id.text_view_note_daily_feelings_value);

        controlQuestionContentTv = view.findViewById(R.id.text_view_control_question_content_value);
        controlQuestionAnswerTv = view.findViewById(R.id.text_view_control_question_answer_value);
        controlQuestionResultTv = view.findViewById(R.id.text_view_control_question_result_value);

        phoneMovementValueTv = view.findViewById(R.id.phoneMovementValueTextView);
        phoneLocalizationValueTv = view.findViewById(R.id.phoneLocalizationValueTextView);

        fitbitDataCv = view.findViewById(R.id.fitbitDataCardView);
        TextView fitbitDataLabelTextView = view.findViewById(R.id.text_view_fitbit_data_label);
        TextView fitbitStepsLabelTextView = view.findViewById(R.id.text_view_fitbit_steps_label);
        fitbitStepsValueTv = view.findViewById(R.id.text_view_fitbit_steps_value);

        setupPersonalData(presentDate, List.of(dateValueTextView, nameValueTextView, birthdateValueTextView), database);
        setupDailyFeelingsData(presentDate);
        setupControlQuestion(presentDate);
        setupPhoneMovementData();
        setupPhoneLocalizationData(presentDate);
        //System.out.println(sharedPreferences.getBoolean(getString(R.string.switchState), false));
        if (isFitbitEnable()) {
            setFitbitDataVisible();
            // TODO: simplify fitbit data and setText to textView
            setupFitbitData(presentDate);
        }
    }

    private boolean isFitbitEnable() {
        return FitbitUtils.isEnabled(sharPref, getString(R.string.switch_state));
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
                .getByDate(presentDate);
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

    private void setupPhoneMovementData() {
        String europeanDatePattern = "dd.MM.yyyy HH:mm";
        DateTimeFormatter europeanDateFormatter = DateTimeFormatter.ofPattern(europeanDatePattern);

        PhoneMovement phoneMovement = database.phoneMovementDao().getNewestMovement().orElse(new PhoneMovement());
        phoneMovementValueTv.setText(phoneMovement.getTimeOfMovement().toString());
    }

    private void setupPhoneLocalizationData(LocalDate presentDate) throws IOException {
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

//        FitbitSpO2Data fitbitSpO2Data = database.fitbitSpO2DataDao().getNewestFitbitSpO2DataByDate(presentDate)
//                .orElse(new FitbitSpO2Data());

        fitbitStepsValueTv.setText(fitbitStepsData.getStepsValue());
//        fitbitSp02ValueTextView.setText(fitbitSpO2Data.getSpO2Value());
    }
}