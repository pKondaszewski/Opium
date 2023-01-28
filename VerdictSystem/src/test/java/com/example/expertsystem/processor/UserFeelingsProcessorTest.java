package com.example.expertsystem.processor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UserFeelingsProcessorTest {

    @Test
    public void processAndGetHighAccuratePositiveResult() {
        UserFeelingsProcessor processor = new UserFeelingsProcessor(4.0, 1.0);

        Double result = processor.process();

        assertEquals(Double.valueOf(4.0), result);
    }

    @Test
    public void processAndGetMediumAccuratePositiveResult() {
        UserFeelingsProcessor processor = new UserFeelingsProcessor(3.0, 0.0);

        Double result = processor.process();

        assertEquals(Double.valueOf(3.0), result);
    }

    @Test
    public void processAndGetLowAccuratePositiveResult() {
        UserFeelingsProcessor processor = new UserFeelingsProcessor(0.0, 0.0);

        Double result = processor.process();

        assertEquals(Double.valueOf(1.0), result);
    }
}
