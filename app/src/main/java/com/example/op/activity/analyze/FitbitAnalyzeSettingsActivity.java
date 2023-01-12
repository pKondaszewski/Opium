package com.example.op.activity.analyze;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.NumberPicker;

import com.example.op.R;
import com.example.op.activity.extra.GlobalSetupAppCompatActivity;

import java.time.LocalDate;

public class FitbitAnalyzeSettingsActivity extends GlobalSetupAppCompatActivity {
    private Context context;
    private NumberPicker yearNp;
    private SharedPreferences sharPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitbit_analyze_settings);

        LocalDate now = LocalDate.now();
        context = getApplicationContext();
        sharPref = context.getSharedPreferences(context.getString(com.example.database.R.string.opium_preferences), Context.MODE_PRIVATE);
        int yearValue = sharPref.getInt(context.getString(com.example.database.R.string.year_on_chart), now.getYear());

        yearNp = findViewById(R.id.number_picker_year_value);
        yearNp.setMinValue(2020);
        yearNp.setMaxValue(now.getYear());
        yearNp.setValue(yearValue);

        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_OK, returnIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sharPref.edit()
                .putInt(context.getString(com.example.database.R.string.year_on_chart), yearNp.getValue())
                .apply();
    }
}
