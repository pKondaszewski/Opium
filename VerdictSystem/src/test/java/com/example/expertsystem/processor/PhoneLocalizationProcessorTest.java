package com.example.expertsystem.processor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.List;

public class PhoneLocalizationProcessorTest {

    @Test
    public void processAndGetHighAccuratePositiveResult() {
        PhoneLocalizationProcessor processor = new PhoneLocalizationProcessor(List.of(1.0, 1.0), List.of(1.0, 1.0));

        Double result = processor.process();

        assertEquals(Double.valueOf(5.0), result);
    }

    @Test
    public void processAndGetMediumAccuratePositiveResult() {
        PhoneLocalizationProcessor processor = new PhoneLocalizationProcessor(List.of(1.0, 1.0), List.of(1.002, 1.002));

        Double result = processor.process();

        assertEquals(Double.valueOf(3.0), result);
    }

    @Test
    public void processAndGetLowAccuratePositiveResult() {
        PhoneLocalizationProcessor processor = new PhoneLocalizationProcessor(List.of(1.0, 1.0), List.of(2.0, 2.0));

        Double result = processor.process();

        assertEquals(Double.valueOf(1.0), result);
    }
}
