package com.example.expertsystem.engine;

import com.example.expertsystem.ExpertSystem;

import java.math.RoundingMode;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class PhoneActivityEngine extends FuzzyLogicEngine {

    private static final String TAG = PhoneActivityEngine.class.getName();
    private final Integer notedMovements;
    private final Double phoneLocalizationEngineResult;
    private List<TreeMap<Double, Double>> therms, therms1;
    private TreeMap<Double, Double> therm1, therm2, therm3, therm4, therm5,
            therm11, therm22, therm33, therm44, therm55;

    public PhoneActivityEngine(Integer notedMovements, Double phoneLocalizationEngineResult) {
        super(TAG);
        this.notedMovements = notedMovements;
        this.phoneLocalizationEngineResult = phoneLocalizationEngineResult;

        therm1 = new TreeMap<>();
        therm2 = new TreeMap<>();
        therm3 = new TreeMap<>();
        therm4 = new TreeMap<>();
        therm5 = new TreeMap<>();
        therms = List.of(therm1, therm2, therm3, therm4, therm5);

        therm11 = new TreeMap<>();
        therm22 = new TreeMap<>();
        therm33 = new TreeMap<>();
        therm44 = new TreeMap<>();
        therm55 = new TreeMap<>();
        therms1 = List.of(therm11, therm22, therm33, therm44, therm55);
    }

    @Override
    public Double process() {
        URL resource = ExpertSystem.class.getResource("/therms/phone_movement.csv");
        URL resource2 = ExpertSystem.class.getResource("/therms/phone_localization.csv");
        fillTherms(therms, Objects.requireNonNull(resource));
        fillTherms(therms1, Objects.requireNonNull(resource2));

        double notedMovementAsDouble = notedMovements.doubleValue();
        List<ArrayList<Double>> inferenceResult = inference(notedMovementAsDouble, phoneLocalizationEngineResult);
        List<Double> maxValues = inferenceResult.stream()
                .map(doubles -> doubles.stream().max(Double::compare).get())
                .collect(Collectors.toList());
        return defuzzyficate(maxValues);
    }

    @Override
    protected List<ArrayList<Double>> inference(Double notedMovement, Double phoneLocalizationEngineResult) {
        Double var1 = therm1.get(notedMovement);
        Double var2 = therm2.get(notedMovement);
        Double var3 = therm3.get(notedMovement);
        Double var4 = therm4.get(notedMovement);
        Double var5 = therm5.get(notedMovement);

        Double var11 = therm11.get(phoneLocalizationEngineResult);
        Double var22 = therm22.get(phoneLocalizationEngineResult);
        Double var33 = therm33.get(phoneLocalizationEngineResult);
        Double var44 = therm44.get(phoneLocalizationEngineResult);
        Double var55 = therm55.get(phoneLocalizationEngineResult);

        ArrayList<Double> rules1 = new ArrayList<>();
        ArrayList<Double> rules2 = new ArrayList<>();
        ArrayList<Double> rules3 = new ArrayList<>();

        rules1.add(Math.min(var1, var11));
        rules1.add(Math.min(var1, var22));
        rules1.add(Math.min(var1, var33));
        rules2.add(Math.min(var1, var44));
        rules2.add(Math.min(var1, var55));

        rules1.add(Math.min(var2, var11));
        rules1.add(Math.min(var2, var22));
        rules2.add(Math.min(var2, var33));
        rules2.add(Math.min(var2, var44));
        rules2.add(Math.min(var2, var55));

        rules1.add(Math.min(var3, var11));
        rules2.add(Math.min(var3, var22));
        rules2.add(Math.min(var3, var33));
        rules2.add(Math.min(var3, var44));
        rules3.add(Math.min(var3, var55));

        rules2.add(Math.min(var4, var11));
        rules2.add(Math.min(var4, var22));
        rules2.add(Math.min(var4, var33));
        rules3.add(Math.min(var4, var44));
        rules3.add(Math.min(var4, var55));

        rules2.add(Math.min(var5, var11));
        rules2.add(Math.min(var5, var22));
        rules3.add(Math.min(var5, var33));
        rules3.add(Math.min(var5, var44));
        rules3.add(Math.min(var5, var55));

        return List.of(rules1, rules2, rules3);
    }

    @Override
    protected Double defuzzyficate(List<Double> maxValues) {
        Double max1 = maxValues.get(0);
        Double max2 = maxValues.get(1);
        Double max3 = maxValues.get(2);
        return (max1 * 1 + max2 * 2 + max3 * 3) / (max1 + max2 + max3);
    }
}
