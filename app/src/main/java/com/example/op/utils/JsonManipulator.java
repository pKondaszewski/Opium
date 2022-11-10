package com.example.op.utils;

import android.util.Log;

import com.example.database.entity.DailyFeelings;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JsonManipulator {

    private static final String TAG = JsonManipulator.class.getName();
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

    public static String extractSteps(String response) {
        String value = null;
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("activities-steps");
            value = (String) jsonArray.getJSONObject(0).get("value");
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return value;
    }


    public static String extractSpo2(String response) {
        String value = null;
        try {
            String spO2Values = new JSONObject(response).getString("value");
            value = new JSONObject(spO2Values).getString("avg");
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return value;
    }

    public static String stringifyDailyFeelings(DailyFeelings dailyFeelings) {
        try {
            return objectMapper.writeValueAsString(dailyFeelings);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "There are unresolved problems with stringify dailyFeelings object into json: " +
                    dailyFeelings);
        }
        return null;
    }

    public static DailyFeelings parseDailyFeelings(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, DailyFeelings.class);
        } catch (JsonProcessingException e) {
            Log.e(TAG, "There are unresolved problems with parsing dailyFeelings json string: " +
                    jsonString);
        }
        return null;
    }
}
