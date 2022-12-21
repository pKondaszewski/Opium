package com.example.expertsystem.extractor;

import com.example.database.AppDatabase;
import com.example.database.entity.ControlTextQuestion;
import com.example.database.entity.ControlTextUserAnswer;
import com.example.database.entity.DailyFeelings;
import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;
import com.example.database.entity.PhoneLocalization;
import com.example.database.entity.PhoneMovement;
import com.example.expertsystem.ExpertSystemLevel;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class DataExtractor {

    private final AppDatabase database;
    private final LocalDate now;

    public DataExtractor(AppDatabase database) {
        this.database = database;
        now = LocalDate.now();
    }

    public List<Double> extractMostCommonCoordinates() {
        Optional<PhoneLocalization> mostCommonLocationByDate = database.phoneLocalizationDao()
                .getMostCommonLocationByDate(now);
        if (mostCommonLocationByDate.isPresent()) {
            PhoneLocalization phoneLocalization = mostCommonLocationByDate.get();
            return Arrays.asList(phoneLocalization.getLatitude(), phoneLocalization.getLongitude());
        } else {
            return null;
        }
    }

    public Integer extractAmountOfNotedMovements(ExpertSystemLevel level) {
        List<PhoneMovement> allByDate = database.phoneMovementDao().getAllByDate(now);
        int levelValue = switch (level) {
            case LOW -> 1;
            case MEDIUM -> 0;
            case HIGH -> -1;
        };
        int size = allByDate.size() + levelValue;
        return Math.min(size, 4);
    }

    public Double extractMoodValue() {
        Optional<DailyFeelings> byDate = database.dailyFeelingsDao().getByDate(now);
        if (byDate.isPresent()) {
            DailyFeelings dailyFeelings = byDate.get();
            return dailyFeelingsToDoubleValue(dailyFeelings);
        } else {
            return null;
        }
    }

    private Double dailyFeelingsToDoubleValue(DailyFeelings dailyFeelings) {
        double moodValue = switch (DailyFeelingsEnum.valueOf(dailyFeelings.getMood().toUpperCase(Locale.ROOT))) {
            case TRAGIC -> 0.0;
            case BAD -> 1.0;
            case OK -> 2.0;
            case GOOD -> 3.0;
            case GREAT -> 4.0;
        };
        List<String> ailments = dailyFeelings.getAilments();
        moodValue -= ailments.get(0).equals("") ? 0 : ailments.size() * 0.2;
        return Math.max(moodValue, 0.0);
    }

    public Double extractAnswerValue() {
        Optional<ControlTextUserAnswer> byDate = database.controlTextUserAnswerDao().getNewestByDate(now);
        if (byDate.isPresent()) {
            ControlTextUserAnswer controlTextUserAnswer = byDate.get();
            Integer controlTextQuestionId = controlTextUserAnswer.getControlTextQuestionId();
            ControlTextQuestion controlTextQuestion = database.controlTextQuestionDao().getById(controlTextQuestionId).get();
            return controlTextUserAnswer.getUserAnswer().equals(controlTextQuestion.getCorrectAnswer()) ? 1.0 : 0.0;
        } else {
            return 0.0;
        }
    }

    public Map<String, Integer> extractFitbitData() {
        Optional<FitbitStepsData> newestFitbitStepsDataByDate = database.fitbitStepsDataDao().getNewestFitbitStepsDataByDate(now);
        Optional<FitbitSpO2Data> newestFitbitSpO2DataByDate = database.fitbitSpO2DataDao().getNewestFitbitSpO2DataByDate(now);
        int stepsValue, spO2Value;
        if (newestFitbitStepsDataByDate.isPresent()) {
            FitbitStepsData fitbitStepsData = newestFitbitStepsDataByDate.get();
            stepsValue = Integer.parseInt(fitbitStepsData.getStepsValue());
        } else {
            stepsValue = 0;
        }
        if (newestFitbitSpO2DataByDate.isPresent()) {
            FitbitSpO2Data fitbitSpO2Data = newestFitbitSpO2DataByDate.get();
            spO2Value = Integer.parseInt(fitbitSpO2Data.getSpO2Value());
        } else {
            spO2Value = 0;
        }
        return Map.of("fitbitStepsData", stepsValue,"fitbitSpO2Data", spO2Value);
    }
}
