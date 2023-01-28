package com.example.op.activity.analyze;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.database.AppDatabase;
import com.example.database.entity.ExpertSystemResult;
import com.example.expertsystem.ExpertSystem;
import com.example.expertsystem.ExpertSystemLevel;
import com.example.expertsystem.ResourceNotFoundException;
import com.example.op.R;
import com.example.op.activity.extra.GlobalSetupAppCompatActivity;

import java.time.LocalDate;
import java.util.Locale;

public class ExpertSystemActivity extends GlobalSetupAppCompatActivity implements Runnable {
    private static final String TAG = ExpertSystemActivity.class.getName();
    private static final String DOUBLE_FORMAT = "%.1f";
    private SharedPreferences sharPref;
    private TextView userMovementResultValue, locationResultValue, userFeelingsResultValue,
            fitbitStepsResultValueTv, fitbitSpo2ResultValueTv, expertSystemFitbitResultLabelTv,
            expertFitbitStepsResultLabelTv, expertFitbitSpO2ResultLabelTv, expertSystemFitbitDescriptionTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_expert_system);

        userMovementResultValue = findViewById(R.id.text_view_expert_system_user_movement_result_value);
        locationResultValue = findViewById(R.id.text_view_expert_system_location_result_value);
        userFeelingsResultValue = findViewById(R.id.text_view_expert_system_user_feelings_result_value);
        expertSystemFitbitResultLabelTv = findViewById(R.id.text_view_expert_system_fitbit_result_label);
        expertFitbitStepsResultLabelTv = findViewById(R.id.text_view_expert_fitbit_steps_result_label);
        expertFitbitSpO2ResultLabelTv = findViewById(R.id.text_view_expert_fitbit_spo2_result_label);
        fitbitStepsResultValueTv = findViewById(R.id.text_view_expert_fitbit_steps_result_value);
        fitbitSpo2ResultValueTv = findViewById(R.id.text_view_expert_fitbit_spo2_result_value);
        expertSystemFitbitDescriptionTv = findViewById(R.id.text_view_expert_system_fitbit_description);

        sharPref = getSharedPreferences(getString(com.example.database.R.string.opium_preferences), MODE_PRIVATE);
        setFitbitVisibility();

        AppDatabase database = AppDatabase.getInstance(this);
        database.expertSystemResultDao().getByDate(LocalDate.now()).ifPresent(this::setupTextViews);

        Button launchExpertSystemBtn = findViewById(R.id.button_launch_expert_system);
        launchExpertSystemBtn.setOnClickListener(v -> {
            Thread thread = new Thread(this);
            thread.start();
        });
    }

    private void setFitbitVisibility() {
        String isFitbitEnabled = sharPref.getString(getString(com.example.database.R.string.fitbit_switch_state), "false");
        if (Boolean.parseBoolean(isFitbitEnabled)) {
            expertSystemFitbitResultLabelTv.setVisibility(View.VISIBLE);
            expertFitbitStepsResultLabelTv.setVisibility(View.VISIBLE);
            expertFitbitSpO2ResultLabelTv.setVisibility(View.VISIBLE);
            fitbitStepsResultValueTv.setVisibility(View.VISIBLE);
            fitbitSpo2ResultValueTv.setVisibility(View.VISIBLE);
            expertSystemFitbitDescriptionTv.setVisibility(View.VISIBLE);
        }
    }

    private void setupTextViews(ExpertSystemResult result) {
        String NA = getString(R.string.none_data);;
        Locale localeDefault = Locale.getDefault();
        Double userMovementResult = result.getUserMovementResult();
        Double locationResult = result.getLocationResult();
        Double fitbitStepsResult = result.getFitbitStepsResult();
        Double fitbitSpO2Result = result.getFitbitSpO2Result();
        Double userFeelingsResult = result.getUserFeelingsResult();

        userMovementResultValue.setText(userMovementResult == null ?
                NA : String.format(localeDefault, DOUBLE_FORMAT, userMovementResult));
        locationResultValue.setText(locationResult == null ?
                NA : String.format(localeDefault, DOUBLE_FORMAT, locationResult));
        fitbitStepsResultValueTv.setText(fitbitStepsResult == null ?
                NA : String.format(localeDefault, DOUBLE_FORMAT, fitbitStepsResult));
        fitbitSpo2ResultValueTv.setText(fitbitSpO2Result == null ?
                NA : String.format(localeDefault, DOUBLE_FORMAT, fitbitSpO2Result));
        userFeelingsResultValue.setText(userFeelingsResult == null ?
                NA : String.format(localeDefault, DOUBLE_FORMAT, userFeelingsResult));
    }

    @Override
    public void run() {
        ExpertSystem expertSystem = new ExpertSystem(this);
        try {
            String level = sharPref.getString(getString(com.example.database.R.string.expert_system_level), "LOW");
            expertSystem.launch(ExpertSystemLevel.valueOf(level));
            String message = "Expert system processing has finished successfully";
            Log.i(TAG, message);
            runOnUiThread(
                    () -> Toast.makeText(ExpertSystemActivity.this, message, Toast.LENGTH_SHORT).show());
            finish();
        } catch (ResourceNotFoundException e) {
            Log.e(TAG, e.getMessage());
            runOnUiThread(
                    () -> Toast.makeText(ExpertSystemActivity.this, e.getMessage(), Toast.LENGTH_LONG).show());
        }
    }
}
