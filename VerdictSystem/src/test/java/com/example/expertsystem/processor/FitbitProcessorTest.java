package com.example.expertsystem.processor;

import static org.junit.Assert.assertEquals;

import com.example.expertsystem.ExpertSystemLevel;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class FitbitProcessorTest {

    private String fitbitStepsResult, fitbitSpO2Result;

    @Before
    public void init() {
        fitbitStepsResult = "fitbitStepsResult";
        fitbitSpO2Result = "fitbitSpO2Result";
    }

    @Test
    public void processWithLowExpertSystemLevel() {
        FitbitProcessor processor = new FitbitProcessor(10, 55, ExpertSystemLevel.LOW);

        Map<String, Double> process = processor.process();

        assertEquals(Double.valueOf(1.0), process.get(fitbitStepsResult));
        assertEquals(Double.valueOf(1.0), process.get(fitbitSpO2Result));
    }

    @Test
    public void processWithMediumExpertSystemLevel() {
        FitbitProcessor processor = new FitbitProcessor(400, 80, ExpertSystemLevel.MEDIUM);

        Map<String, Double> process = processor.process();

        assertEquals(Double.valueOf(2.0), process.get(fitbitStepsResult));
        assertEquals(Double.valueOf(2.0), process.get(fitbitSpO2Result));
    }

    @Test
    public void processWithHighExpertSystemLevel() {
        FitbitProcessor processor = new FitbitProcessor(4000, 95, ExpertSystemLevel.HIGH);

        Map<String, Double> process = processor.process();

        assertEquals(Double.valueOf(3.0), process.get(fitbitStepsResult));
        assertEquals(Double.valueOf(3.0), process.get(fitbitSpO2Result));
    }
}
