package com.example.op.utils;

import android.content.SharedPreferences;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FitbitUtils {

    public boolean isEnabled(SharedPreferences sharedPreferences, String key) {
        return sharedPreferences.getBoolean(key, false);
    }
}