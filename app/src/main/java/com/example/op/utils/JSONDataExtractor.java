package com.example.op.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONDataExtractor {

    public static String steps(String response) {
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


    public static String spO2(String response) {
        String value = null;
        try {
            String spO2Values = new JSONObject(response).getString("value");
            value = new JSONObject(spO2Values).getString("avg");
        } catch (JSONException exception) {
            exception.printStackTrace();
        }
        return value;
    }
}
