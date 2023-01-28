package com.example.expertsystem.processor;

import com.example.expertsystem.ExpertSystemLevel;

import java.util.Map;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FitbitProcessor {

    private final Integer stepsValue, spO2Value;
    private final ExpertSystemLevel level;

    public Map<String, Double> process() {
        Double stepsResult = calculateSteps();
        Double spO2Result = calculateSpO2();
        return Map.of("fitbitStepsResult", stepsResult, "fitbitSpO2Result", spO2Result);
    }

    private Double calculateSteps() {
        Integer stepsValueByLevel = getStepsValueByLevel();
        if (stepsValue < 100 * stepsValueByLevel) {
            return 1.0;
        } else if (stepsValue > 100 * stepsValueByLevel && stepsValue < 500 * stepsValueByLevel) {
            return 2.0;
        } else {
            return 3.0;
        }
    }

    private Double calculateSpO2() {
        Integer spO2ValueByLevel = getSpO2ValueByLevel();
        if (spO2Value < spO2ValueByLevel) {
            return 1.0;
        } else if (spO2Value > spO2ValueByLevel && spO2Value < spO2ValueByLevel + 10) {
            return 2.0;
        } else {
            return 3.0;
        }
    }

    private Integer getStepsValueByLevel() {
        switch (level) {
            case LOW: return 1;
            case MEDIUM: return 3;
            case HIGH: return 6;
            default: return null;
        }
    }

    private Integer getSpO2ValueByLevel() {
        switch (level) {
            case LOW: return 65;
            case MEDIUM: return 75;
            case HIGH: return 85;
            default: return null;
        }
    }
}
