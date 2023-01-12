package com.example.op.activity.extra;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.op.utils.LocaleHelper;

import java.util.Locale;

public class GlobalSetupAppCompatActivity extends AppCompatActivity {
    private Locale currentLocale;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.updateResources(base));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onStart() {
        super.onStart();
        currentLocale = getResources().getConfiguration().locale;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Locale locale = getLocale(getApplicationContext());
        if (!locale.equals(currentLocale)) {
            currentLocale = locale;
            LocaleHelper.updateResources(getApplicationContext());
            recreate();
        }
    }

    public static Locale getLocale(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(com.example.database.R.string.opium_preferences), MODE_PRIVATE);
        String lang = sharedPreferences.getString("language", "EN");
        switch (lang) {
            case "EN":
                lang = "EN";
                break;
            case "PL":
                lang = "PL";
                break;
        }
        return new Locale(lang);
    }
}