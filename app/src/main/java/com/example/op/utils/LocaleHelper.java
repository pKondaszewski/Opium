package com.example.op.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.example.op.R;

import java.util.Locale;

public class LocaleHelper {

    public static void setLocale(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.opium_preferences), Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "en");

        System.out.println(language);

        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
    }

    public static Context updateResources(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.opium_preferences), Context.MODE_PRIVATE);
        String language = sharedPreferences.getString("language", "en");

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }
}