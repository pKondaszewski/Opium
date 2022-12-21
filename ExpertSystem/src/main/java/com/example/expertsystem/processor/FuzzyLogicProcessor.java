package com.example.expertsystem.processor;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import lombok.Cleanup;

public abstract class FuzzyLogicProcessor {

    private final String TAG;

    public FuzzyLogicProcessor(String TAG) {
        this.TAG = TAG;
    }

    public abstract Double process();

    protected abstract List<List<Double>> inference(Double var1, Double var2);

    protected abstract Double defuzzyficate(List<Double> maxValues);

    protected void fillTherms(List<TreeMap<Double, Double>> therms, URL csvFilePath, int size) {
        try {
            @Cleanup BufferedReader br = new BufferedReader(new InputStreamReader(csvFilePath.openStream()));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.charAt(0) == '/') {
                    continue;
                }
                List<Double> oneLineElementsFromCsvFile = Arrays.stream(line.split(","))
                        .map(Double::parseDouble).collect(Collectors.toList());
                Double argValue = oneLineElementsFromCsvFile.get(0);
                for (int i = 0; i < size; i++) {
                    TreeMap<Double, Double> therm = therms.get(i);
                    Double elementValue = oneLineElementsFromCsvFile.get(i+1); //i+1
                    therm.put(argValue, elementValue);
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "There are unresolved IOException while reading terms from .csv files");
        }
    }
}
