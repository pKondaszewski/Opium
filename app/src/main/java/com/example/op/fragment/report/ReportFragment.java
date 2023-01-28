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
import com.example.database.entity.EmailContact;
import com.example.database.entity.ExpertSystemResult;
import com.example.database.entity.PhoneContact;
import com.example.database.entity.Profile;
import com.example.op.R;
import com.example.op.email.SendMailTask;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public abstract class ReportFragment extends Fragment {
    protected final DateTimeFormatter dateTimeFormatter = DateTimeFormatter
            .ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault());
    protected final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    protected abstract void setupPersonalData(LocalDate presentDate, List<TextView> personalDataViews, AppDatabase database);

    protected boolean sendMail(String title, String message, Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED) {
            AppDatabase database = AppDatabase.getInstance(context);
            List<EmailContact> emailContacts = database.emailContactDao().getAll();
            emailContacts.forEach(emailContact -> handleEmailSending(
                    context, emailContact.getEmailAddress(), title, message)
            );
            return true;
        } else {
            return false;
        }
    }

    private void handleEmailSending(Context context, String emailAddress, String title, String message) {
        SendMailTask t = new SendMailTask(context, emailAddress, title, message, false);
        t.execute();
    }

    protected boolean sendSms(String TAG, String message, Context context) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            AppDatabase database = AppDatabase.getInstance(context);
            ArrayList<String> dividedMessage = smsManager.divideMessage(message);
            List<PhoneContact> phoneContacts = database.phoneContactDao().getAll();
            phoneContacts.forEach(phoneContact -> {
                        String formattedNumber = phoneContact.getPhoneNumber().replaceAll("\\s+", "");
                        smsManager.sendMultipartTextMessage(formattedNumber, null, dividedMessage, null, null);
                    });
            if (!phoneContacts.isEmpty()) {
                Toast.makeText(context, R.string.sms_report_success, Toast.LENGTH_SHORT).show();
                Log.i(TAG, "SMS report sent successfully");
            }
            return true;
        } else {
            Toast.makeText(context, getString(R.string.sms_no_permission_message), Toast.LENGTH_SHORT).show();
            Log.e(TAG, "SMS report sent failure");
            return false;
        }
    }

    protected String getGeneralReportMessage(Context context) {
        LocalDate now = LocalDate.now();
        AppDatabase database = AppDatabase.getInstance(context);
        SharedPreferences sharPref = context.getSharedPreferences(context.getString(com.example.database.R.string.opium_preferences), Context.MODE_PRIVATE);
        String isFitbitEnabled = sharPref.getString(getString(com.example.database.R.string.fitbit_switch_state), "false");
        Profile profile = database.profileDao().get().orElse(new Profile());
        return String.format("%s: %s %s\n", getString(R.string.name_label_text_view), profile.getFirstname(), profile.getLastname()) +
               String.format("%s: %s", getString(R.string.analyze_result_label_text_view), setupResults(now, Boolean.parseBoolean(isFitbitEnabled), database));
    }

    protected String getDetailedReportMessage(Context context) {
        LocalDate now = LocalDate.now();
        AppDatabase database = AppDatabase.getInstance(context);
        SharedPreferences sharPref = context.getSharedPreferences(context.getString(com.example.database.R.string.opium_preferences), Context.MODE_PRIVATE);
        String isFitbitEnabledString = sharPref.getString(getString(com.example.database.R.string.fitbit_switch_state), "false");
        boolean isFitbitEnabled = Boolean.parseBoolean(isFitbitEnabledString);
        ReportBuilder rb = new ReportBuilder(context);
        return  rb.getProfile() + rb.getBirthdate() + rb.getMood() + rb.getAilments() + rb.getNote() +
                rb.getTimeOfDailyFeelings() + rb.getTextQuestion() + rb.getUserAnswer() + rb.getCorrectAnswer() +
                rb.getTimeOfAnswer() + rb.getPhoneMovementAverageTime() + rb.getPhoneMovementCount() +
                rb.getMostCountedHomeAddress() + rb.getFitbitStepsScore(isFitbitEnabled) + rb.getFitbitSpO2Score(isFitbitEnabled) +
                String.format("%s: %s", getString(R.string.analyze_result_label_text_view), setupResults(now, isFitbitEnabled, database));
    }

    protected String setupResults(LocalDate presentDate, boolean isFitbitEnabled, AppDatabase database) {
        Optional<ExpertSystemResult> resultOptional = database.expertSystemResultDao().getByDate(presentDate);
        if (resultOptional.isPresent()) {
            ExpertSystemResult result = resultOptional.get();
            String maxPoints;
            Double finalResult;
            if (isFitbitEnabled) {
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
            return getString(R.string.none_data);
        }
    }
}
