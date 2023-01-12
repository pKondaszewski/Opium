package com.example.expertsystem.processor;

import com.example.expertsystem.ExpertSystem;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class UserFeelingsProcessor extends FuzzyLogicProcessor {
    private static final String TAG = PhoneActivityProcessor.class.getName();
    private final Double moodValue, answerValue;
    private final List<TreeMap<Double, Double>> therms, therms1;
    private final TreeMap<Double, Double> therm1, therm2, therm3, therm4, therm5,
            therm11, therm22;

    public UserFeelingsProcessor(Double moodValue, Double answerValue) {
        super(TAG);
        this.moodValue = moodValue;
        this.answerValue = answerValue;

        therm1 = new TreeMap<>();
        therm2 = new TreeMap<>();
        therm3 = new TreeMap<>();
        therm4 = new TreeMap<>();
        therm5 = new TreeMap<>();
        therms = List.of(therm1, therm2, therm3, therm4, therm5);

        therm11 = new TreeMap<>();
        therm22 = new TreeMap<>();
        therms1 = List.of(therm11, therm22);
    }

    @Override
    public Double process() {
        URL resource = ExpertSystem.class.getResource("/therms/mood.csv");
        URL resource1 = ExpertSystem.class.getResource("/therms/answer.csv");
        fillTherms(therms, resource, therms.size());
        fillTherms(therms1, resource1, therms1.size());

        List<List<Double>> inferenceResult = inference(moodValue, answerValue);
        List<Double> maxValues = inferenceResult.stream()
                .map(doubles -> doubles.stream().max(Double::compare).get())
                .collect(Collectors.toList());
        return defuzzyficate(maxValues);
    }

    @Override
    protected List<List<Double>> inference(Double moodValue, Double answerValue) {
        Double var1 = therm1.get(moodValue);
        Double var2 = therm2.get(moodValue);
        Double var3 = therm3.get(moodValue);
        Double var4 = therm4.get(moodValue);
        Double var5 = therm5.get(moodValue);

        Double var11 = therm11.get(answerValue);
        Double var22 = therm22.get(answerValue);

        ArrayList<Double> rules0 = new ArrayList<>();
        ArrayList<Double> rules1 = new ArrayList<>();
        ArrayList<Double> rules2 = new ArrayList<>();
        ArrayList<Double> rules3 = new ArrayList<>();

        rules0.add(Math.min(var1, var11));
        rules1.add(Math.min(var2, var11));
        rules2.add(Math.min(var3, var11));
        rules2.add(Math.min(var4, var11));
        rules2.add(Math.min(var5, var11));

        rules1.add(Math.min(var1, var22));
        rules2.add(Math.min(var2, var22));
        rules2.add(Math.min(var3, var22));
        rules2.add(Math.min(var4, var22));
        rules3.add(Math.min(var5, var22));

        return List.of(rules0, rules1, rules2, rules3);
    }

    @Override
    protected Double defuzzyficate(List<Double> maxValues) {
        Double max0 = maxValues.get(0);
        Double max1 = maxValues.get(1);
        Double max2 = maxValues.get(2);
        Double max3 = maxValues.get(3);
        return (max0 * 1 + max1 * 2 + max2 * 3 + max3 * 4) / (max0 + max1 + max2 + max3);
    }
}
