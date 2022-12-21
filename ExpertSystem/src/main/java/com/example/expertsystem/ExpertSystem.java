package com.example.expertsystem;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.example.database.AppDatabase;
import com.example.database.dao.ExpertSystemResultDao;
import com.example.database.entity.ExpertSystemResult;
import com.example.database.entity.Profile;
import com.example.expertsystem.extractor.DataExtractor;
import com.example.expertsystem.processor.FitbitProcessor;
import com.example.expertsystem.processor.PhoneActivityProcessor;
import com.example.expertsystem.processor.PhoneLocalizationProcessor;
import com.example.expertsystem.processor.UserFeelingsProcessor;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class ExpertSystem {
    private static final String TAG = ExpertSystem.class.getName();
    private final AppDatabase database;
    private final Context context;
    private final DataExtractor extractor;
    private final SharedPreferences sharPref;

    public ExpertSystem(Context context) {
        this.context = context;
        database = AppDatabase.getDatabaseInstance(context);
        extractor = new DataExtractor(database);
        sharPref = context.getSharedPreferences(context.getString(R.string.opium_preferences), Context.MODE_PRIVATE);
    }

    public void launch(ExpertSystemLevel level) {
        try {
            Map<String, Double> paResults = getPhoneActivityResults(level);
            Map<String, Double> fitbitResults = getFitbitResults(level);
            Map<String, Double> uaResults = getUserActivityResults();
            Double finalResult = calculateFinalResult(paResults, fitbitResults, uaResults);
            String string = sharPref.getString(context.getString(com.example.database.R.string.fitbit_switch_state), "false");

            ExpertSystemResult result = new ExpertSystemResult(null, LocalDate.now(), LocalTime.now(),
                    paResults.get("phoneLocalizationProcessorResult"), paResults.get("phoneActivityResult"),
                    fitbitResults.get("fitbitStepsResult"), fitbitResults.get("fitbitSpO2Result"),
                    uaResults.get("userFeelingsResult"), finalResult);

            ExpertSystemResultDao dao = database.expertSystemResultDao();
            if (dao.getNewestByDate(LocalDate.now()).isPresent()) {
                dao.update(result);
            } else {
                dao.insert(result);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public Map<String, Double> getPhoneActivityResults(ExpertSystemLevel level) throws IOException {
        Double phoneLocalizationProcessorResult = getPhoneLocalizationResult();

        Integer amountOfNotedMovements = extractor.extractAmountOfNotedMovements(level);
        PhoneActivityProcessor phoneActivityProcessor = new PhoneActivityProcessor(amountOfNotedMovements, phoneLocalizationProcessorResult, level);
        Double phoneActivityResult = phoneActivityProcessor.process();

        Log.i(TAG,"phoneActivityResult: " + phoneActivityResult);
        return Map.of("phoneLocalizationProcessorResult", phoneLocalizationProcessorResult,
                "phoneActivityResult", phoneActivityResult);
    }

    public Double getPhoneLocalizationResult() throws IOException {
        try {
            List<Double> mostCommonCoordinates = extractor.extractMostCommonCoordinates();
            if (mostCommonCoordinates == null) {
                return 1.0;
            }
            List<Double> profileCoordinates = getProfileCoordinates(context);
            Double phoneLocalizationProcessorResult;
            PhoneLocalizationProcessor phoneLocalizationProcessor = new PhoneLocalizationProcessor(mostCommonCoordinates, profileCoordinates);
            phoneLocalizationProcessorResult = phoneLocalizationProcessor.process();
            Log.i(TAG,"phoneLocalizationProcessorResult: " + phoneLocalizationProcessorResult);
            return phoneLocalizationProcessorResult;
        } catch (ResourceNotFoundException e) {
            Log.e(TAG, "Profile has no address");
            return 0.0;
        }
    }

    public Map<String, Double> getFitbitResults(ExpertSystemLevel level) {
        Map<String, Integer> fitbitMap = extractor.extractFitbitData();
        FitbitProcessor fitbitProcessor = new FitbitProcessor(fitbitMap.get("fitbitStepsData"), fitbitMap.get("fitbitSpO2Data"), level);
        return fitbitProcessor.process();
    }

    public Map<String, Double> getUserActivityResults() {
        Double moodValue = extractor.extractMoodValue();
        Double answerValue = extractor.extractAnswerValue();

        Double userFeelingsResult = 1.0;
        if (moodValue != null) {
            UserFeelingsProcessor userFeelingsProcessor = new UserFeelingsProcessor(moodValue, answerValue);
            userFeelingsResult = userFeelingsProcessor.process();
        }

        Log.i(TAG,"userFeelingsResult: " + userFeelingsResult);
        return Map.of("userFeelingsResult", userFeelingsResult);
    }

    private Double calculateFinalResult(Map<String, Double> phoneActivityResults,
                                        Map<String, Double> fitbitResults,
                                        Map<String, Double> userActivityResults) {
        double sum = phoneActivityResults.values().stream().mapToDouble(value -> value).sum();  // 1-5 1-3
        sum += fitbitResults.values().stream().mapToDouble(value -> value).sum();               // 1-3 1-3
        sum += userActivityResults.values().stream().mapToDouble(value -> value).sum();         // 1-4
        return sum;
    }

    private List<Double> getProfileCoordinates(Context context) throws IOException, ResourceNotFoundException {
        Profile profile = database.profileDao().get().orElseThrow(ResourceNotFoundException::new);
        Geocoder geocoder = new Geocoder(context);
        List<Address> location = geocoder.getFromLocationName(profile.getHomeAddress().getStreetAddress(), 1);
        double latitude = location.get(0).getLatitude();
        double longitude = location.get(0).getLongitude();
        return List.of(latitude, longitude);
    }
}
