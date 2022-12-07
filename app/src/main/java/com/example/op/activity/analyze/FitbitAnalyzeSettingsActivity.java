package com.example.op.activity.analyze;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.NumberPicker;

import com.example.op.R;
import com.example.op.activity.extra.TranslatedAppCompatActivity;

import java.time.LocalDate;

public class FitbitAnalyzeSettingsActivity extends TranslatedAppCompatActivity {

    private Context context;
    private NumberPicker numberPicker;
    private SharedPreferences sharPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitbit_analyze_settings);
        context = getApplicationContext();
        sharPref = context.getSharedPreferences(context.getString(R.string.opium_preferences), Context.MODE_PRIVATE);
        int initValue = sharPref.getInt(context.getString(R.string.days_on_chart), 7);

        numberPicker = findViewById(R.id.number_picker_days_value);
        numberPicker.setMinValue(3);
        numberPicker.setMaxValue(LocalDate.now().getDayOfYear()-1);
        numberPicker.setValue(initValue);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharPref.edit()
                .putInt(context.getString(R.string.days_on_chart), numberPicker.getValue())
                .apply();
    }
}
