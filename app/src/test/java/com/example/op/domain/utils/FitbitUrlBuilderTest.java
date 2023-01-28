package com.example.op.domain.utils;

import static org.junit.Assert.assertEquals;

import com.example.op.utils.FitbitUrlBuilder;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

public class FitbitUrlBuilderTest {
    private static final String STEPS_URL_FORMAT = "https://api.fitbit.com/1/user/-/activities/steps/date/%s/1d.json";
    private static final String SPO2_URL_FORMAT = "https://api.fitbit.com/1/user/-/spo2/date/%s.json";
    private LocalDate date1, date2, date3;

    @Before
    public void init() {
        date1 = LocalDate.of(1,1,1);
        date2 = LocalDate.of(2,2,2);
        date3 = LocalDate.of(3,3,3);
    }

    @Test
    public void getStepsUrl() {
        String date1Url = FitbitUrlBuilder.stepsUrl(date1);
        String date2Url = FitbitUrlBuilder.stepsUrl(date2);
        String date3Url = FitbitUrlBuilder.stepsUrl(date3);

        assertEquals(String.format(STEPS_URL_FORMAT, date1), date1Url);
        assertEquals(String.format(STEPS_URL_FORMAT, date2), date2Url);
        assertEquals(String.format(STEPS_URL_FORMAT, date3), date3Url);
    }

    @Test
    public void getSpO2Url() {
        String date1Url = FitbitUrlBuilder.spO2Url(date1);
        String date2Url = FitbitUrlBuilder.spO2Url(date2);
        String date3Url = FitbitUrlBuilder.spO2Url(date3);

        assertEquals(String.format(SPO2_URL_FORMAT, date1), date1Url);
        assertEquals(String.format(SPO2_URL_FORMAT, date2), date2Url);
        assertEquals(String.format(SPO2_URL_FORMAT, date3), date3Url);
    }
}
