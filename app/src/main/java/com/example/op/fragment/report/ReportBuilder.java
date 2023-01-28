package com.example.op.fragment.report;

import android.content.Context;

import com.example.database.AppDatabase;
import com.example.database.entity.DailyFeelings;
import com.example.database.entity.DailyQuestion;
import com.example.database.entity.DailyQuestionAnswer;
import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;
import com.example.database.entity.PhoneLocalization;
import com.example.database.entity.Profile;
import com.example.op.R;
import com.example.op.utils.Translation;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.OptionalDouble;

public class ReportBuilder {
    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter
            .ofLocalizedDate(FormatStyle.MEDIUM).withLocale(Locale.getDefault());
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    private final Context ctx;
    private final AppDatabase database;
    private final Translation translation;
    private final DailyFeelings dailyFeelings;
    private final DailyQuestionAnswer userAnswer;
    private final LocalTime phoneMovementDate;
    private final Integer dailyPhoneMovementCount;
    private final PhoneLocalization phoneLocalization;
    private final FitbitStepsData fitbitStepsData;
    private final FitbitSpO2Data fitbitSpO2Data;
    private DailyQuestion question;
    private Profile profile;
    private String none;

    public ReportBuilder(Context context) {
        this.ctx = context;
        database = AppDatabase.getInstance(context);
        translation = new Translation(context);
        LocalDate now = LocalDate.now();
        none = ctx.getString(R.string.none_data);

        profile = database.profileDao().get().orElse(new Profile());
        dailyFeelings = database.dailyFeelingsDao().getByDate(now).orElse(new DailyFeelings());
        userAnswer = database.dailyQuestionAnswerDao().getNewestByDate(now).orElse(new DailyQuestionAnswer());
        question = null;
        if (userAnswer.getDailyQuestionId() != null) {
            question = database.dailyQuestionDao().getById(userAnswer.getDailyQuestionId()).orElse(new DailyQuestion());
        }
        List<LocalTime> averageTimeOfMovementsByDate = database.phoneMovementDao().getAllTimeByDate(now);
        OptionalDouble average = averageTimeOfMovementsByDate.stream().mapToInt(LocalTime::toSecondOfDay).average();
        dailyPhoneMovementCount = database.phoneMovementDao().getCountByDate(now);
        phoneMovementDate = average.isPresent() ? LocalTime.ofSecondOfDay((long) average.getAsDouble()) : null;
        phoneLocalization = database.phoneLocalizationDao().getMostCommonLocationByDate(now).orElse(new PhoneLocalization());
        fitbitStepsData = database.fitbitStepsDataDao().getMaxFitbitStepsDataByDate(now).orElse(new FitbitStepsData());
        fitbitSpO2Data = database.fitbitSpO2DataDao().getMaxFitbitSpO2DataByDate(now).orElse(new FitbitSpO2Data());
    }

    public String getProfile() {
        String firstname = profile.getFirstname();
        String lastname = profile.getLastname();
        String resultFirstname = Objects.nonNull(firstname) && !Objects.equals(firstname, "") ? firstname : "-";
        String resultLastname = Objects.nonNull(lastname) && !Objects.equals(firstname, "") ? lastname : "-";
        return String.format("%s: %s %s\n", ctx.getString(R.string.name_label_text_view),
                resultFirstname, resultLastname);
    }

    public String getBirthdate() {
        return String.format("%s: %s\n", ctx.getString(R.string.birthdate_profile_text_view), dateTimeFormatter.format(profile.getBirthdate()));
    }

    public String getMood() {
        String mood = dailyFeelings.getMood();
        String resultMood = Objects.nonNull(mood) && !Objects.equals(mood, "") ? translation.translateMood(dailyFeelings.getMood()) : none;
        return String.format("\n\n%s: %s\n", ctx.getString(R.string.mood_text_view), resultMood);
    }

    public String getAilments() {
        List<String> ailments = dailyFeelings.getAilments();
        String resultAilments = Objects.nonNull(ailments) && !ailments.isEmpty() ?
                String.join(", ", translation.translateAilments(dailyFeelings.getAilments())) : none;
        return String.format("%s: %s\n", ctx.getString(R.string.ailments_text_view), resultAilments);
    }

    public String getNote() {
        String note = dailyFeelings.getNote();
        String resultNote = Objects.nonNull(note) && !Objects.equals(note, "") ? note : none;
        return String.format("%s: %s\n", ctx.getString(R.string.note_text_view), resultNote);
    }

    public String getTimeOfDailyFeelings() {
        return String.format("%s: %s\n\n", ctx.getString(R.string.time_of_save_daily_feelings_text_view),
                Objects.nonNull(dailyFeelings.getTimeOfDailyFeelings()) ?
                        timeFormatter.format(dailyFeelings.getTimeOfDailyFeelings()) : none);
    }

    public String getTextQuestion() {
        String questionString = Objects.nonNull(question) ? question.getTextQuestion() : none;
        return String.format("%s: %s\n", ctx.getString(R.string.daily_question_label_text_view), questionString);
    }

    public String getUserAnswer() {
        String userAns = Objects.nonNull(userAnswer.getUserAnswer()) ? userAnswer.getUserAnswer().toString() : none;
        return String.format("%s: %s\n", ctx.getString(R.string.daily_question_answer_label_text_view), userAns);
    }

    public String getCorrectAnswer() {
        String correctAns = Objects.nonNull(question) ? question.getCorrectAnswer().toString() : none;
        return String.format("%s: %s\n", ctx.getString(R.string.daily_question_result_label_text_view), correctAns);
    }

    public String getTimeOfAnswer() {
        String saveTime = Objects.nonNull(userAnswer.getTimeOfAnswer()) ?
                timeFormatter.format(userAnswer.getTimeOfAnswer()) : none;
        return String.format("%s: %s\n\n", ctx.getString(R.string.daily_question_time_of_save_text_view), saveTime);
    }

    public String getPhoneMovementAverageTime() {
        String averageTime = Objects.nonNull(phoneMovementDate)  ? timeFormatter.format(phoneMovementDate) : none;
        return String.format("%s: %s\n", ctx.getString(R.string.phone_movement_report_label), averageTime);
    }

    public String getPhoneMovementCount() {
        return String.format("%s: %s\n", ctx.getString(R.string.daily_phone_movement_label_text_view),
                dailyPhoneMovementCount);
    }

    public String getMostCountedHomeAddress() {
        String address = Objects.nonNull(phoneLocalization.getHomeAddress()) ?
                phoneLocalization.getHomeAddress().toSimpleString() : none;
        return String.format("%s: %s\n\n", ctx.getString(R.string.phone_localization_label_text_view), address);
    }

    public String getFitbitStepsScore(Boolean isFitbitEnabled) {
        if (isFitbitEnabled) {
            return String.format("%s: %s\n", ctx.getString(R.string.fitbit_steps_label_text_view),
                    Objects.equals(fitbitStepsData.getStepsValue(), "-") ? none : fitbitStepsData.getStepsValue());
        } else {
            return "";
        }
    }

    public String getFitbitSpO2Score(Boolean isFitbitEnabled) {
        if (isFitbitEnabled) {
            return String.format("%s: %s\n\n", ctx.getString(R.string.fitbit_spo2_label_text_view),
                    Objects.equals(fitbitSpO2Data.getSpO2Value(), "-") ? none : fitbitSpO2Data.getSpO2Value());
        } else {
            return "";
        }
    }
}
