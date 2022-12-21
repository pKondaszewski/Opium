package com.example.op.activity.extra;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;

import com.example.op.R;
import com.example.op.utils.LocaleHelper;

import java.util.Locale;

public class TranslatedAppCompatActivity extends AppCompatActivity {

    private Locale currentLocale;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.updateResources(base));
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
        String lang = sharedPreferences.getString("language", "en");
        switch (lang) {
            case "en":
                lang = "en";
                break;
            case "pl":
                lang = "pl";
                break;
        }
        return new Locale(lang);
    }
}