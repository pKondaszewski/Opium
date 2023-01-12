package com.example.expertsystem.processor;

import androidx.annotation.NonNull;

import com.example.expertsystem.ExpertSystem;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PhoneLocalizationProcessor extends FuzzyLogicProcessor {
    private static final String TAG = PhoneLocalizationProcessor.class.getName();
    private final List<Double> mostCommonCoordinates, profileCoordinates;
    private final TreeMap<Double, Double> therm1, therm2, therm3, therm4, therm5;
    private final List<TreeMap<Double, Double>> therms;

    public PhoneLocalizationProcessor(List<Double> mostCommonCoordinates, List<Double> profileCoordinates) {
        super(TAG);
        this.mostCommonCoordinates = mostCommonCoordinates;
        this.profileCoordinates = profileCoordinates;

        therm1 = new TreeMap<>();
        therm2 = new TreeMap<>();
        therm3 = new TreeMap<>();
        therm4 = new TreeMap<>();
        therm5 = new TreeMap<>();
        therms = List.of(therm1, therm2, therm3, therm4, therm5);
    }

    @Override
    public Double process() {
        URL resource = ExpertSystem.class.getResource("/therms/localization.csv");
        fillTherms(therms, Objects.requireNonNull(resource), 5);

        List<Double> parsedCoordinatesDifferences = getParsedCoordinatesDifferences();
        if (parsedCoordinatesDifferences.isEmpty()) {
            return 1.0;
        }
        Double parsedLatitudeDifference = parsedCoordinatesDifferences.get(0);
        Double parsedLongitudeDifference = parsedCoordinatesDifferences.get(1);

        List<List<Double>> inferenceResult = inference(parsedLatitudeDifference, parsedLongitudeDifference);
        List<Double> maxValues = inferenceResult.stream()
                .map(doubles -> doubles.stream().max(Double::compare).get())
                .collect(Collectors.toList());
        return defuzzyficate(maxValues);
    }

    @Override
    protected List<List<Double>> inference(Double latitude, Double longitude) {
        latitude = latitude == -0.0 ? 0.0 : latitude;
        longitude = longitude == -0.0 ? 0.0 : longitude;

        if (latitude < -0.006 || latitude > 0.006 || longitude < -0.006 || longitude > 0.006) {
            List<Double> listWithZero = List.of(0.0);
            return List.of(List.of(1.0), listWithZero, listWithZero, listWithZero, listWithZero);
        }

        Double var1 = therm1.get(latitude);
        Double var2 = therm2.get(latitude);
        Double var3 = therm3.get(latitude);
        Double var4 = therm4.get(latitude);
        Double var5 = therm5.get(latitude);

        Double var11 = therm1.get(longitude);
        Double var22 = therm2.get(longitude);
        Double var33 = therm3.get(longitude);
        Double var44 = therm4.get(longitude);
        Double var55 = therm5.get(longitude);

        ArrayList<Double> rules1 = new ArrayList<>();
        ArrayList<Double> rules2 = new ArrayList<>();
        ArrayList<Double> rules3 = new ArrayList<>();
        ArrayList<Double> rules4 = new ArrayList<>();
        ArrayList<Double> rules5 = new ArrayList<>();

        rules1.add(Math.min(var1, var11));
        rules2.add(Math.min(var1, var22));
        rules3.add(Math.min(var1, var33));
        rules2.add(Math.min(var1, var44));
        rules1.add(Math.min(var1, var55));

        rules2.add(Math.min(var2, var11));
        rules3.add(Math.min(var2, var22));
        rules4.add(Math.min(var2, var33));
        rules3.add(Math.min(var2, var44));
        rules2.add(Math.min(var2, var55));

        rules3.add(Math.min(var3, var11));
        rules4.add(Math.min(var3, var22));
        rules5.add(Math.min(var3, var33));
        rules4.add(Math.min(var3, var44));
        rules3.add(Math.min(var3, var55));

        rules2.add(Math.min(var4, var11));
        rules3.add(Math.min(var4, var22));
        rules4.add(Math.min(var4, var33));
        rules3.add(Math.min(var4, var44));
        rules2.add(Math.min(var4, var55));

        rules1.add(Math.min(var5, var11));
        rules2.add(Math.min(var5, var22));
        rules3.add(Math.min(var5, var33));
        rules2.add(Math.min(var5, var44));
        rules1.add(Math.min(var5, var55));

        return List.of(rules1, rules2, rules3, rules4, rules5);
    }

    @NonNull
    private List<Double> getParsedCoordinatesDifferences() {
        if (profileCoordinates.isEmpty()) {
            return Collections.emptyList();
        }
        Double profileLatitude = profileCoordinates.get(0);
        Double profileLongitude = profileCoordinates.get(1);
        Double mostCommonLatitude = mostCommonCoordinates.get(0);
        Double mostCommonLongitude = mostCommonCoordinates.get(1);

        double latitudeDifference = mostCommonLatitude - profileLatitude;
        double longitudeDifference = mostCommonLongitude - profileLongitude;
        Double parsedLatitudeDifference = Math.floor(latitudeDifference * 10000) / 10000;
        Double parsedLongitudeDifference = Math.floor(longitudeDifference * 10000) / 10000;

        return List.of(parsedLatitudeDifference, parsedLongitudeDifference);
    }

    @Override
    protected Double defuzzyficate(List<Double> maxValues) {
        Double max1 = maxValues.get(0);
        Double max2 = maxValues.get(1);
        Double max3 = maxValues.get(2);
        Double max4 = maxValues.get(3);
        Double max5 = maxValues.get(4);
        return (max1 * 1 + max2 * 2 + max3 * 3 + max4 * 4 + max5 * 5) / (max1 + max2 + max3 + max4 + max5);
    }
}
