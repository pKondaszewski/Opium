package com.example.op.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LocalDateUtils {
    public static LocalDate extractFromSharPref(Context context) {
        SharedPreferences sharPref = context.getSharedPreferences(context.getString(com.example.database.R.string.opium_preferences), Context.MODE_PRIVATE);
        LocalDate localDateNow = LocalDate.now();
        int dayOfYear = localDateNow.getDayOfYear();
        int nowYear = localDateNow.getYear();
        int year = sharPref.getInt(context.getString(com.example.database.R.string.year_on_chart), nowYear);
        return LocalDate.ofYearDay(year, dayOfYear);
    }

    public static String monthFromInt(int monthValue) {
        return monthValue > 9 ?
                String.valueOf(monthValue) :
                String.format(Locale.getDefault(), "0%d", monthValue);
    }

    public static String yearFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    public static int extractNumberOfPastDaysFromYear(LocalDate date) {
        LocalDate now = LocalDate.now();
        if (date.isEqual(now)) {
            return now.getDayOfYear();
        } else {
            boolean leapYear = date.getYear() % 4 == 0;
            return leapYear ? 366 : 365;
        }
    }

    public static int extractNumberOfPastMonthsFromYear(LocalDate date) {
        LocalDate now = LocalDate.now();
        return date.isEqual(now) ? now.getMonthValue() : 12;
    }
}
