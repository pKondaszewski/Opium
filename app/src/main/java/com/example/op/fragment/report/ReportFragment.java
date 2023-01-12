package com.example.op.fragment.report;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.database.AppDatabase;
import com.example.database.entity.DailyFeelings;
import com.example.database.entity.DailyQuestion;
import com.example.database.entity.DailyQuestionAnswer;
import com.example.database.entity.EmailContact;
import com.example.database.entity.ExpertSystemResult;
import com.example.database.entity.PhoneContact;
import com.example.database.entity.PhoneLocalization;
import com.example.database.entity.Profile;
import com.example.op.R;
import com.example.op.email.SendMail;
import com.example.op.utils.Translation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class ReportFragment extends Fragment {
    protected final DateTimeFormatter dateTimeFormatter = DateTimeFormatter
                                                            .ofLocalizedDate(FormatStyle.MEDIUM)
                                                            .withLocale(Locale.getDefault());
    protected final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final Translation translation = new Translation(getContext());

    protected abstract void setupPersonalData(LocalDate presentDate, List<TextView> personalDataViews, AppDatabase database);

    protected boolean sendMail(String TAG, String title, String message, Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            AppDatabase database = AppDatabase.getDatabaseInstance(context);
            List<EmailContact> emailContacts = database.emailContactDao().getAll();
            emailContacts.forEach(emailContact -> {
                        SendMail sendMail = new SendMail(context, emailContact.getEmailAddress(), title, message);
                        executor.execute(sendMail);
                    }
            );
            if (!emailContacts.isEmpty()) {
                Toast.makeText(context, "Email report sent", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Email report sent successfully");
            }
            return true;
        } else {
            return false;
        }
    }

    protected boolean sendSms(String TAG, String message, Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            AppDatabase database = AppDatabase.getDatabaseInstance(context);
            ArrayList<String> dividedMessage = smsManager.divideMessage(message);
            List<PhoneContact> phoneContacts = database.phoneContactDao().getAll();
            phoneContacts.forEach(phoneContact -> {
                        String formattedNumber = phoneContact.getPhoneNumber().replaceAll("\\s+", "");
                        smsManager.sendMultipartTextMessage(formattedNumber, null, dividedMessage, null, null);
                    });
            Toast.makeText(context, "SMS report sent", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "SMS report sent successfully");
            return true;
        } else {
            Log.i(TAG, "SMS report sent failure");
            return false;
        }
    }

    protected String getGeneralReportMessage(Context context) {
        LocalDate now = LocalDate.now();
        AppDatabase database = AppDatabase.getDatabaseInstance(context);
        SharedPreferences sharPref = context.getSharedPreferences(context.getString(com.example.database.R.string.opium_preferences), Context.MODE_PRIVATE);
        Profile profile = database.profileDao().get().orElse(new Profile());

        return String.format("%s: %s %s\n", getString(R.string.name_label_text_view), profile.getFirstname(), profile.getLastname()) +
               String.format("%s: %s", getString(R.string.analyze_result_label_text_view), setupResults(now, sharPref, database));
    }

    protected String getDetailedReportMessage(Context context) {
        LocalDate now = LocalDate.now();
        AppDatabase database = AppDatabase.getDatabaseInstance(context);
        SharedPreferences sharPref = context.getSharedPreferences(context.getString(com.example.database.R.string.opium_preferences), Context.MODE_PRIVATE);
        Profile profile = database.profileDao().get().orElse(new Profile());
        DailyFeelings dailyFeelings = database.dailyFeelingsDao().getByDate(now).orElse(new DailyFeelings());
        DailyQuestionAnswer userAnswer = database.dailyQuestionAnswerDao().getNewestByDate(now).orElse(new DailyQuestionAnswer());
        DailyQuestion question = null;
        if (userAnswer.getDailyQuestionId() != null) {
            question = database.dailyQuestionDao().getById(userAnswer.getDailyQuestionId()).orElse(new DailyQuestion());
        }
        List<LocalTime> averageTimeOfMovementsByDate = database.phoneMovementDao().getAllTimeByDate(now);
        OptionalDouble average = averageTimeOfMovementsByDate.stream().mapToInt(LocalTime::toSecondOfDay).average();

        LocalTime phoneMovementDate = average.isPresent() ? LocalTime.ofSecondOfDay((long) average.getAsDouble()) : null;
        PhoneLocalization phoneLocalization = database.phoneLocalizationDao().getMostCommonLocationByDate(now).orElse(new PhoneLocalization());

        return  String.format("%s: %s %s\n", getString(R.string.name_label_text_view), profile.getFirstname(), profile.getLastname()) +
                String.format("%s: %s\n", getString(R.string.birthdate_profile_text_view), dateTimeFormatter.format(profile.getBirthdate()) +
                String.format("\n\n%s: %s\n", getString(R.string.mood_text_view), dailyFeelings.getMood() == null ? "" : translation.translateMood(dailyFeelings.getMood())) +
                String.format("%s: %s\n", getString(R.string.ailments_text_view), dailyFeelings.getAilments() == null ? "" : String.join(",", translation.translateAilments(dailyFeelings.getAilments()))) +
                String.format("%s: %s\n", getString(R.string.note_text_view), dailyFeelings.getNote() == null ? "" : dailyFeelings.getNote()) +
                String.format("%s: %s\n\n", getString(R.string.time_of_save_daily_feelings_text_view), dailyFeelings.getTimeOfDailyFeelings() == null ? "" : timeFormatter.format(dailyFeelings.getTimeOfDailyFeelings())) +
                String.format("%s: %s\n", getString(R.string.daily_question_label_text_view), question == null ? "" : question.getTextQuestion()) +
                String.format("%s: %s\n", getString(R.string.daily_question_answer_label_text_view), userAnswer.getUserAnswer() == null ? "" : userAnswer.getUserAnswer()) +
                String.format("%s: %s\n", getString(R.string.daily_question_result_label_text_view), question == null ? "" : question.getCorrectAnswer()) +
                String.format("%s: %s\n\n", getString(R.string.daily_question_time_of_save_text_view), userAnswer.getTimeOfAnswer() == null ? "" : timeFormatter.format(userAnswer.getTimeOfAnswer())) +
                String.format("%s: %s\n", getString(R.string.phone_movement_label_text_view), timeFormatter.format(phoneMovementDate)) +
                String.format("%s: %s\n", getString(R.string.phone_localization_label_text_view), phoneLocalization.getHomeAddress() == null ? "" : phoneLocalization.getHomeAddress().toSimpleString()) +
                String.format("%s: %s", getString(R.string.analyze_result_label_text_view), setupResults(now, sharPref, database)));
    }

    protected String setupResults(LocalDate presentDate, SharedPreferences sharPref, AppDatabase database) {
        Optional<ExpertSystemResult> resultOptional = database.expertSystemResultDao().getByDate(presentDate);
        if (resultOptional.isPresent()) {
            ExpertSystemResult result = resultOptional.get();
            String maxPoints;
            Double finalResult;
            String isFitbitEnabled = sharPref.getString(getString(com.example.database.R.string.fitbit_switch_state), "false");
            if (Boolean.parseBoolean(isFitbitEnabled)) {
                maxPoints = String.format(Locale.getDefault(), "%.1f", 18.0);
                finalResult = result.getFinalResult();
            } else {
                maxPoints = String.format(Locale.getDefault(), "%.1f", 12.0);
                Double fitbitStepsResult = result.getFitbitStepsResult() == null ? 0.0 : result.getFitbitStepsResult();
                Double fitbitSpO2Result = result.getFitbitSpO2Result() == null ? 0.0 : result.getFitbitSpO2Result();
                finalResult = result.getFinalResult() - fitbitStepsResult - fitbitSpO2Result;
            }
            return String.format(Locale.getDefault(), "%.1f/%s", finalResult, maxPoints);
        } else {
            return "N/A";
        }
    }
}
