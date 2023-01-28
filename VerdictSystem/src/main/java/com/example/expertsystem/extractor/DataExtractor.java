package com.example.expertsystem.extractor;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.example.database.AppDatabase;
import com.example.database.entity.DailyFeelings;
import com.example.database.entity.DailyQuestion;
import com.example.database.entity.DailyQuestionAnswer;
import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;
import com.example.database.entity.PhoneLocalization;
import com.example.database.entity.Profile;
import com.example.expertsystem.ExpertSystemLevel;
import com.example.expertsystem.ResourceNotFoundException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class DataExtractor {
    private final String TAG = DataExtractor.class.getName();
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

    public List<Double> extractProfileCoordinates(Context context) throws IOException, ResourceNotFoundException {
        Profile profile = database.profileDao().get().get();
        Geocoder geocoder = new Geocoder(context);
        if (profile.getHomeAddress() == null) {
            throw new ResourceNotFoundException(TAG, "Profile has no address");
        }
        List<Address> location = geocoder.getFromLocationName(profile.getHomeAddress().toSimpleString(), 1);
        if (Objects.isNull(location) || location.isEmpty()) {
            return Collections.emptyList();
        }
        Address address = location.get(0);
        return List.of(address.getLatitude(), address.getLongitude());
    }

    public Integer extractAmountOfNotedMovements(ExpertSystemLevel level) {
        Integer count = database.phoneMovementDao().getCountByDate(now);
        int levelValue;
        switch (level) {
            case LOW:
                levelValue = 1;
                break;
            case MEDIUM:
                levelValue = 0;
                break;
            case HIGH:
                levelValue = -1;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + level);
        }
        int size = count + levelValue;
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
        double moodValue = 0.0;
        switch (DailyFeelingsEnum.valueOf(dailyFeelings.getMood().toUpperCase(Locale.ROOT))) {
            case TRAGIC:
                moodValue = 0.0;
                break;
            case BAD:
                moodValue = 1.0;
                break;
            case OK:
                moodValue = 2.0;
                break;
            case GOOD:
                moodValue = 3.0;
                break;
            case GREAT:
                moodValue = 4.0;
                break;
        };
        List<String> ailments = dailyFeelings.getAilments();
        moodValue -= ailments.get(0).equals("") ? 0 : ailments.size() * 0.2;
        return Math.max(moodValue, 0.0);
    }

    public Double extractAnswerValue() {
        Optional<DailyQuestionAnswer> byDate = database.dailyQuestionAnswerDao().getNewestByDate(now);
        if (byDate.isPresent()) {
            DailyQuestionAnswer dailyQuestionAnswer = byDate.get();
            Integer dailyQuestionId = dailyQuestionAnswer.getDailyQuestionId();
            DailyQuestion dailyQuestion = database.dailyQuestionDao().getById(dailyQuestionId).get();
            return dailyQuestionAnswer.getUserAnswer().equals(dailyQuestion.getCorrectAnswer()) ? 1.0 : 0.0;
        } else {
            return 0.0;
        }
    }

    public Map<String, Integer> extractFitbitData() {
        Optional<FitbitStepsData> newestFitbitStepsDataByDate = database.fitbitStepsDataDao().getMaxFitbitStepsDataByDate(now);
        Optional<FitbitSpO2Data> newestFitbitSpO2DataByDate = database.fitbitSpO2DataDao().getMaxFitbitSpO2DataByDate(now);
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
