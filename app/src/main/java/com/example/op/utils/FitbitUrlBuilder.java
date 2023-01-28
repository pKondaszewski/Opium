package com.example.op.utils;

import java.time.LocalDate;

public class FitbitUrlBuilder {

    private static final String baseUrl = "https://api.fitbit.com/1/user/-/";

    public static String stepsUrl(LocalDate date) {
        return baseUrl + "activities/steps/date/" + date + "/1d.json";
    }

    public static String spO2Url(LocalDate date) {
        return baseUrl + "spo2/date/" + date + ".json";
    }
}
