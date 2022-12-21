package com.example.op.fragment.report;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.database.AppDatabase;
import com.example.database.entity.ControlTextQuestion;
import com.example.database.entity.ControlTextUserAnswer;
import com.example.database.entity.DailyFeelings;
import com.example.database.entity.ExpertSystemResult;
import com.example.database.entity.Profile;
import com.example.op.R;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public abstract class ReportFragment extends Fragment {

    private final Context context = getContext();
    private final AppDatabase database = AppDatabase.getDatabaseInstance(context);

    protected abstract void setupPersonalData(LocalDate presentDate, List<TextView> personalDataViews, AppDatabase database);

    protected abstract boolean sendMail();

    protected abstract boolean sendSms();

    protected String generateTextMessage(SharedPreferences sharPref) {
        LocalDate now = LocalDate.now();
        Profile profile = database.profileDao().get().orElse(new Profile());
        DailyFeelings dailyFeelings = database.dailyFeelingsDao().getByDate(now).orElse(new DailyFeelings());
        ControlTextUserAnswer userAnswer = database.controlTextUserAnswerDao().getNewestByDate(now).orElse(new ControlTextUserAnswer());
        ControlTextQuestion question;
        if (userAnswer.getId() != null) {
            question = database.controlTextQuestionDao().getById(userAnswer.getId()).orElse(new ControlTextQuestion());
        }
        //ExpertSystemResult eSResult = database.expertSystemResultDao().getNewestByDate(now).orElse(new ExpertSystemResult());

        return String.format("%s: %s\n", getString(R.string.report_date_text_view), now.toString()) +
               String.format("%s: %s %s\n", getString(R.string.name_label_text_view), profile.getFirstname(), profile.getLastname()) +
               String.format("%s: %s\n", getString(R.string.analyze_result_label_text_view), setupResults(now, sharPref));
    }

    protected String setupResults(LocalDate presentDate, SharedPreferences sharPref) {
        Optional<ExpertSystemResult> resultOptional = database.expertSystemResultDao().getNewestByDate(presentDate);
        if (resultOptional.isPresent()) {
            ExpertSystemResult result = resultOptional.get();
            String maxPoints;
            Double finalResult;
            String isFitbitEnabled = sharPref.getString(getString(com.example.database.R.string.fitbit_switch_state), "false");
            if (Boolean.parseBoolean(isFitbitEnabled)) {
                maxPoints = "18.0";
                finalResult = result.getFinalResult();
            } else {
                maxPoints = "12.0";
                Double fitbitStepsResult = result.getFitbitStepsResult() == null ? 0.0 : result.getFitbitStepsResult();
                Double fitbitSpO2Result = result.getFitbitSpO2Result() == null ? 0.0 : result.getFitbitSpO2Result();
                finalResult = result.getFinalResult() - fitbitStepsResult - fitbitSpO2Result;
            }
            return String.format("%.1f/%s", finalResult, maxPoints);
        } else {
            return "N/A";
        }
    }
}
