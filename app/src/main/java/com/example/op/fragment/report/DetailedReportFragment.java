package com.example.op.fragment.report;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.example.database.AppDatabase;
import com.example.database.entity.DailyFeelings;
import com.example.database.entity.DailyQuestion;
import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;
import com.example.database.entity.PhoneLocalization;
import com.example.op.R;
import com.example.op.utils.FitbitUtils;
import com.example.op.utils.Translation;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DetailedReportFragment extends GeneralReportFragment {
    private static final String TAG = DetailedReportFragment.class.getName();
    private AppDatabase database;
    private Context context;
    private CardView fitbitDataCv;
    private SharedPreferences sharPref;
    private TextView dateValueTextView, nameValueTextView, birthdateValueTextView, fitbitStepsValueTv,
            fitbitSpO2ValueTv, moodDailyFeelingsValueTv, ailmentsDailyFeelingsValueTv, noteDailyFeelingsValueTv,
            timeOfSaveDailyFeelingsValueTv, phoneMovementValueTv, phoneMovementCountValueTv,
            phoneLocalizationValueTv, dailyQuestionContentTv, dailyQuestionAnswerTv,
            dailyQuestionResultTv, timeOfSaveDailyQuestionAnswerTv, analyzeResultValueTv;
    private Translation translation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        context = requireContext();
        database = AppDatabase.getInstance(context);
        translation = new Translation(context);
        return inflater.inflate(R.layout.fragment_detailed_report, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LocalDate presentDate = LocalDate.now();
        sharPref = getContext().getSharedPreferences(getString(com.example.database.R.string.opium_preferences), Context.MODE_PRIVATE);

        dateValueTextView = view.findViewById(R.id.text_view_label);
        nameValueTextView = view.findViewById(R.id.text_view_name_value);
        birthdateValueTextView = view.findViewById(R.id.text_view_birthdate_value);
        moodDailyFeelingsValueTv = view.findViewById(R.id.text_view_mood_daily_feelings_value);
        ailmentsDailyFeelingsValueTv = view.findViewById(R.id.text_view_ailments_daily_feelings_value);
        noteDailyFeelingsValueTv = view.findViewById(R.id.text_view_note_daily_feelings_value);
        timeOfSaveDailyFeelingsValueTv = view.findViewById(R.id.text_view_time_of_save_daily_feelings_value);

        dailyQuestionContentTv = view.findViewById(R.id.text_view_daily_question_content_value);
        dailyQuestionAnswerTv = view.findViewById(R.id.text_view_daily_question_answer_value);
        dailyQuestionResultTv = view.findViewById(R.id.text_view_daily_question_result_value);
        timeOfSaveDailyQuestionAnswerTv = view.findViewById(R.id.text_view_daily_question_time_of_save_value);

        phoneMovementValueTv = view.findViewById(R.id.text_view_phone_movement_value);
        phoneMovementCountValueTv = view.findViewById(R.id.text_view_phone_movement_count_value);
        phoneLocalizationValueTv = view.findViewById(R.id.text_view_phone_localization_value);

        fitbitDataCv = view.findViewById(R.id.fitbitDataCardView);
        fitbitStepsValueTv = view.findViewById(R.id.text_view_fitbit_steps_value);
        fitbitSpO2ValueTv = view.findViewById(R.id.text_view_fitbit_spo2_value);

        analyzeResultValueTv = view.findViewById(R.id.text_view_analyze_result_value);

        setupData(presentDate);

        Button sendReportBtn = view.findViewById(R.id.button_send_report);
        sendReportBtn.setOnClickListener(v -> {
            boolean isMailSent = sendMail(getString(R.string.report_activity_title), getDetailedReportMessage(context), context);
            boolean isSmsSent = sendSms(TAG, getDetailedReportMessage(context), context);
            if (isMailSent || isSmsSent) {
                requireActivity().finish();
            } else {
                Toast.makeText(context, R.string.no_email_and_sms_send, Toast.LENGTH_LONG).show();
            }
        });
    }


    private void setupData(LocalDate presentDate) {
        try {
            setupPersonalData(presentDate, List.of(dateValueTextView, nameValueTextView, birthdateValueTextView), database);
            setupDailyFeelingsData(presentDate);
            setupDailyQuestion(presentDate);
            setupPhoneMovementData(presentDate);
            setupPhoneLocalizationData();
            if (isFitbitEnable()) {
                setFitbitDataVisible();
                setupFitbitData(presentDate);
            }
            setupDetailedReportResult(presentDate);
        } catch (IOException ioException) {
            Log.e(TAG, "There are ioException during setup data for detailed report", ioException);
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

        moodDailyFeelingsValueTv.setText(translation.translateMood(dailyFeelings.getMood()));
        List<String> ailments = dailyFeelings.getAilments();
        if (ailments != null) {
            ArrayList<String> ailmentsArrayList = new ArrayList<>(ailments);
            int otherIndex = ailmentsArrayList.indexOf("other");
            if (otherIndex != -1) {
                String otherValue = String.format("%s: %s", "other", dailyFeelings.getOtherAilments());
                ailmentsArrayList.set(otherIndex, otherValue);
                ailmentsDailyFeelingsValueTv.setText(String.join(", ", translation.translateAilments(ailmentsArrayList)));
            } else {
                ailmentsDailyFeelingsValueTv.setText(String.join(", ", translation.translateAilments(ailments)));
            }
            noteDailyFeelingsValueTv.setText(dailyFeelings.getNote());
            timeOfSaveDailyFeelingsValueTv.setText(timeFormatter.format(dailyFeelings.getTimeOfDailyFeelings()));
        }
    }

    private void setupDailyQuestion(LocalDate presentDate) {
        database.dailyQuestionAnswerDao().getNewestByDate(presentDate).ifPresent(
                userAnswer -> {
                            Integer questionId = userAnswer.getDailyQuestionId();
                            DailyQuestion dailyQuestion = database.dailyQuestionDao().
                                    getById(questionId).orElse(new DailyQuestion());

                            dailyQuestionContentTv.setText(dailyQuestion.getTextQuestion());
                            dailyQuestionAnswerTv.setText(String.valueOf(userAnswer.getUserAnswer()));
                            dailyQuestionResultTv.setText(String.valueOf(dailyQuestion.getCorrectAnswer()));
                            timeOfSaveDailyQuestionAnswerTv.setText(timeFormatter.format(userAnswer.getTimeOfAnswer()));
                        });
    }

    private void setupPhoneMovementData(LocalDate presentDate) {
        List<LocalTime> averageTimeOfMovementsByDate = database.phoneMovementDao().getAllTimeByDate(presentDate);
        averageTimeOfMovementsByDate.stream().mapToInt(LocalTime::toSecondOfDay).average().ifPresent(
                value -> {
                    LocalTime localTime = LocalTime.ofSecondOfDay((long) value);
                    phoneMovementValueTv.setText(timeFormatter.format(localTime));
                }
        );
        Integer count = database.phoneMovementDao().getCountByDate(presentDate);
        phoneMovementCountValueTv.setText(String.valueOf(count));
    }

    private void setupPhoneLocalizationData() throws IOException {
        PhoneLocalization phoneLocalization = database.phoneLocalizationDao()
                .getMostCommonLocationByDate(LocalDate.now()).orElse(new PhoneLocalization());
        Geocoder geocoder = new Geocoder(getContext());
        String postalAddress = null;
        for (Address address : geocoder.getFromLocation(phoneLocalization.getLatitude(), phoneLocalization.getLongitude(), 1)) {
            postalAddress = address.getAddressLine(0);
        }
        phoneLocalizationValueTv.setText(postalAddress);
    }

    private void setupFitbitData(LocalDate presentDate) {
        FitbitStepsData fitbitStepsData = database.fitbitStepsDataDao().getMaxFitbitStepsDataByDate(presentDate)
                .orElse(new FitbitStepsData());
        FitbitSpO2Data fitbitSpO2Data = database.fitbitSpO2DataDao().getMaxFitbitSpO2DataByDate(presentDate)
                .orElse(new FitbitSpO2Data());

        fitbitStepsValueTv.setText(fitbitStepsData.getStepsValue());
        fitbitSpO2ValueTv.setText(fitbitSpO2Data.getSpO2Value());
    }

    private void setupDetailedReportResult(LocalDate presentDate) {
        String isFitbitEnabledString = sharPref.getString(getString(com.example.database.R.string.fitbit_switch_state), "false");
        boolean isFitbitEnabled = Boolean.parseBoolean(isFitbitEnabledString);
        analyzeResultValueTv.setText(setupResults(presentDate, isFitbitEnabled, database));
    }
}
