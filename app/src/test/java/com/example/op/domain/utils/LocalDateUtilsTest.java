package com.example.op.domain.utils;

import static org.junit.Assert.assertEquals;

import com.example.op.utils.LocalDateUtils;

import org.junit.Test;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class LocalDateUtilsTest {

    @Test
    public void getMonthFromIntPositiveJanuaryResult() {
        String result = LocalDateUtils.monthFromInt(1);
        assertEquals("01",result);
    }

    @Test
    public void getMonthFromIntPositiveJuneResult() {
        String result = LocalDateUtils.monthFromInt(6);
        assertEquals("06",result);
    }

    @Test
    public void getMonthFromIntPositiveNovemberResult() {
        String result = LocalDateUtils.monthFromInt(11);
        assertEquals("11",result);
    }

    @Test
    public void getYearDatePositiveResult() {
        LocalDate localDate = LocalDate.of(2016, 8, 19);
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        String result = LocalDateUtils.yearFromDate(date);

        assertEquals(String.valueOf(localDate.getYear()), result);
    }

    @Test
    public void extractNumberOfPastDaysFromPastYearPositiveResult() {
        LocalDate localDate = LocalDate.of(2017, 8, 19);

        int i = LocalDateUtils.extractNumberOfPastDaysFromYear(localDate);

        assertEquals(365, i);
    }

    @Test
    public void extractNumberOfPastDaysFromPastLeapYearPositiveResult() {
        LocalDate localDate = LocalDate.of(2016, 8, 19);

        int i = LocalDateUtils.extractNumberOfPastDaysFromYear(localDate);

        assertEquals(366, i);
    }

    @Test
    public void extractNumberOfPastDaysFromPresentLeapYearPositiveResult() {
        LocalDate localDate = LocalDate.now();

        int i = LocalDateUtils.extractNumberOfPastDaysFromYear(localDate);

        assertEquals(localDate.getDayOfYear(), i);
    }

    @Test
    public void extractNumberOfPastMonthsFromPresentYearPositiveValue() {
        LocalDate localDate = LocalDate.now();

        int i = LocalDateUtils.extractNumberOfPastMonthsFromYear(localDate);

        assertEquals(localDate.getMonthValue(), i);
    }

    @Test
    public void extractNumberOfPastMonthsFromPastYearPositiveValue() {
        LocalDate localDate = LocalDate.of(1,1,1);

        int i = LocalDateUtils.extractNumberOfPastMonthsFromYear(localDate);

        assertEquals(12, i);
    }
}
