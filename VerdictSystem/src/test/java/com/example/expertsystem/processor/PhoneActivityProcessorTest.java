package com.example.expertsystem.processor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PhoneActivityProcessorTest {

    @Test
    public void processAndGetHighAccuratePositiveResult() {
        PhoneActivityProcessor processor = new PhoneActivityProcessor(6, 5.0, null);

        Double result = processor.process();

        assertEquals(Double.valueOf(3.0), result);
    }

    @Test
    public void processAndGetMediumAccuratePositiveResult() {
        PhoneActivityProcessor processor = new PhoneActivityProcessor(2, 3.0, null);

        Double result = processor.process();

        assertEquals(Double.valueOf(2.0), result);
    }

    @Test
    public void processAndGetLowAccuratePositiveResult() {
        PhoneActivityProcessor processor = new PhoneActivityProcessor(0, 1.0, null);

        Double result = processor.process();

        assertEquals(Double.valueOf(1.0), result);
    }
}
