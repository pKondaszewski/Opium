package com.example.expertsystem;

import android.content.Context;
import android.util.Log;

import com.example.database.AppDatabase;
import com.example.database.dao.ExpertSystemResultDao;
import com.example.database.entity.ExpertSystemResult;
import com.example.expertsystem.extractor.DataExtractor;
import com.example.expertsystem.processor.FitbitProcessor;
import com.example.expertsystem.processor.PhoneActivityProcessor;
import com.example.expertsystem.processor.PhoneLocalizationProcessor;
import com.example.expertsystem.processor.UserFeelingsProcessor;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class ExpertSystem {
    private static final String TAG = ExpertSystem.class.getName();
    private static final String FORMAT = "%s: %.1f";
    private static final String PHONE_ACTIVITY_RESULT = "phoneActivityResult";
    private static final String PHONE_LOCALIZATION_PROCESSOR_RESULT = "phoneLocalizationProcessorResult";
    private static final String FITBIT_STEPS_RESULT = "fitbitStepsResult";
    private static final String FITBIT_SPO2_RESULT = "fitbitSpO2Result";
    private static final String USER_FEELINGS_RESULT = "userFeelingsResult";
    private final AppDatabase database;
    private final Context context;
    private final DataExtractor extractor;

    public ExpertSystem(Context context) {
        this.context = context;
        database = AppDatabase.getInstance(context);
        extractor = new DataExtractor(database);
    }

    public void launch(ExpertSystemLevel level) throws ResourceNotFoundException {
        try {
            Map<String, Double> paResults = getPhoneActivityResults(level);
            Map<String, Double> fitbitResults = getFitbitResults(level);
            Map<String, Double> uaResults = getUserActivityResults();
            Double finalResult = calculateFinalResult(List.of(paResults, fitbitResults, uaResults));

            ExpertSystemResult result = new ExpertSystemResult(null, LocalDate.now(), LocalTime.now(),
                    paResults.get(PHONE_ACTIVITY_RESULT), paResults.get(PHONE_LOCALIZATION_PROCESSOR_RESULT),
                    fitbitResults.get(FITBIT_STEPS_RESULT), fitbitResults.get(FITBIT_SPO2_RESULT),
                    uaResults.get(USER_FEELINGS_RESULT), finalResult);

            ExpertSystemResultDao dao = database.expertSystemResultDao();
            if (dao.getByDate(LocalDate.now()).isPresent()) {
                ExpertSystemResult expertSystemResult = dao.getByDate(LocalDate.now()).get();
                result.setId(expertSystemResult.getId());
                dao.update(result);
            } else {
                dao.insert(result);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public Map<String, Double> getPhoneActivityResults(ExpertSystemLevel level) throws IOException, ResourceNotFoundException {
        Double phoneLocalizationProcessorResult = getPhoneLocalizationResult();
        Integer amountOfNotedMovements = extractor.extractAmountOfNotedMovements(level);
        PhoneActivityProcessor phoneActivityProcessor = new PhoneActivityProcessor(amountOfNotedMovements, phoneLocalizationProcessorResult, level);
        Double phoneActivityResult = phoneActivityProcessor.process();
        Log.i(TAG,String.format(FORMAT, PHONE_ACTIVITY_RESULT, phoneActivityResult));
        return Map.of(PHONE_LOCALIZATION_PROCESSOR_RESULT, phoneLocalizationProcessorResult,
                PHONE_ACTIVITY_RESULT, phoneActivityResult);
    }

    public Double getPhoneLocalizationResult() throws IOException, ResourceNotFoundException {
        List<Double> mostCommonCoordinates = extractor.extractMostCommonCoordinates();
        if (mostCommonCoordinates == null) {
            return 1.0;
        }
        List<Double> profileCoordinates = extractor.extractProfileCoordinates(context);
        PhoneLocalizationProcessor phoneLocalizationProcessor = new PhoneLocalizationProcessor(mostCommonCoordinates, profileCoordinates);
        Double phoneLocalizationProcessorResult = phoneLocalizationProcessor.process();
        Log.i(TAG,String.format(FORMAT, PHONE_LOCALIZATION_PROCESSOR_RESULT, phoneLocalizationProcessorResult));
        return phoneLocalizationProcessorResult;
    }

    public Map<String, Double> getFitbitResults(ExpertSystemLevel level) {
        Map<String, Integer> fitbitMap = extractor.extractFitbitData();
        FitbitProcessor fitbitProcessor = new FitbitProcessor(fitbitMap.get("fitbitStepsData"), fitbitMap.get("fitbitSpO2Data"), level);
        Map<String, Double> fitbitProcessorResults = fitbitProcessor.process();
        Log.i(TAG,String.format(FORMAT, FITBIT_STEPS_RESULT, fitbitProcessorResults.get("fitbitStepsResult")));
        Log.i(TAG,String.format(FORMAT, FITBIT_SPO2_RESULT, fitbitProcessorResults.get("fitbitSpO2Result")));
        return fitbitProcessorResults;
    }

    public Map<String, Double> getUserActivityResults() {
        Double moodValue = extractor.extractMoodValue();
        Double answerValue = extractor.extractAnswerValue();
        Double userFeelingsResult = 1.0;
        if (moodValue != null) {
            UserFeelingsProcessor userFeelingsProcessor = new UserFeelingsProcessor(moodValue, answerValue);
            userFeelingsResult = userFeelingsProcessor.process();
        }
        Log.i(TAG,String.format(FORMAT, USER_FEELINGS_RESULT, userFeelingsResult));
        return Map.of(USER_FEELINGS_RESULT, userFeelingsResult);
    }

    private Double calculateFinalResult(List<Map<String, Double>> resultList) {
        return resultList.stream()
                .mapToDouble(resultMap ->
                        resultMap.values().stream().mapToDouble(value -> value).sum())
                .sum();
    }
}
