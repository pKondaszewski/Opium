package com.example.database;

import androidx.room.TypeConverter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class Converters {
    @TypeConverter
    public static LocalDate timestampToLocalDate(Long value) {
        return value == null ? null : LocalDate.ofEpochDay(value);
    }

    @TypeConverter
    public static Long localDateToTimestamp(LocalDate localDate) {
        return localDate == null ? null : localDate.toEpochDay();
    }

    @TypeConverter
    public static LocalTime stringToLocalTime(String string) {
        return string == null ? null : LocalTime.parse(string);
    }

    @TypeConverter
    public static String localTimeToString(LocalTime localTime) {
        return localTime == null ? null : localTime.toString();
    }

    @TypeConverter
    public static List<String> stringToList(String string) {
        return string == null ? null : List.of(string.split(";"));
    }

    @TypeConverter
    public static String listToString(List<String> list) {
        return list == null ? null : String.join(";", list);
    }

    @TypeConverter
    public static String postalAddressToString(HomeAddress address) {
        return address == null ? null : listToString(
                List.of(address.getStreetAddress(), address.getPostalCode(), address.getCountry()));
    }

    @TypeConverter
    public static HomeAddress stringToPostalAddress(String string) {
        List<String> list = List.of(string.split(";"));
        return switch (list.size()) {
            case 1 -> new HomeAddress(list.get(0), "", "");
            case 2 -> new HomeAddress(list.get(0), list.get(1), "");
            case 3 -> new HomeAddress(list.get(0), list.get(1), list.get(2));
            default -> null;
        };
    }
}
