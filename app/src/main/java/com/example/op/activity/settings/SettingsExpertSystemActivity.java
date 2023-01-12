package com.example.op.activity.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.expertsystem.ExpertSystemLevel;
import com.example.expertsystem.ResourceNotFoundException;
import com.example.op.R;
import com.example.op.activity.extra.GlobalSetupAppCompatActivity;
import com.example.op.utils.simple.SimpleOnSeekBarChangeListener;

import lombok.SneakyThrows;

public class SettingsExpertSystemActivity extends GlobalSetupAppCompatActivity {
    private static final String TAG = SettingsExpertSystemActivity.class.getName();
    private SeekBar expertSystemLevelSb;
    private SharedPreferences sharPref;
    private TextView expertSystemLevelTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expert_system_settings);

        expertSystemLevelTv = findViewById(R.id.text_view_expert_system_level_value);
        expertSystemLevelSb = findViewById(R.id.seek_bar_expert_system_level);

        sharPref = getSharedPreferences(getString(com.example.database.R.string.opium_preferences), MODE_PRIVATE);
        String expertSystemLevel = sharPref.getString(getString(com.example.database.R.string.expert_system_level), "LOW");
        expertSystemLevelTv.setText(expertSystemLevel);
        int expertSystemValue = getExpertSystemValueByLevel(ExpertSystemLevel.valueOf(expertSystemLevel));
        expertSystemLevelSb.setProgress(expertSystemValue);

        expertSystemLevelSb.setOnSeekBarChangeListener(new SimpleOnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    ExpertSystemLevel expertSystemLevel = getExpertSystemLevelByValue(progress);
                    expertSystemLevelTv.setText(expertSystemLevel.toString());
                } catch (ResourceNotFoundException e) {
                    Toast.makeText(SettingsExpertSystemActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private ExpertSystemLevel getExpertSystemLevelByValue(int value) throws ResourceNotFoundException {
        switch (value) {
            case 0: return ExpertSystemLevel.LOW;
            case 1: return ExpertSystemLevel.MEDIUM;
            case 2: return ExpertSystemLevel.HIGH;
            default: throw new ResourceNotFoundException(TAG, "Value from expert system settings seekBar is not 0, 1 or 2.");
        }
    }

    private Integer getExpertSystemValueByLevel(ExpertSystemLevel level) {
        switch (level) {
            case LOW: return 0;
            case MEDIUM: return 1;
            case HIGH: return 2;
            default: return null;
        }
    }

    @SneakyThrows
    @Override
    protected void onPause() {
        super.onPause();
        ExpertSystemLevel expertSystemLevel = getExpertSystemLevelByValue(expertSystemLevelSb.getProgress());
        sharPref.edit()
                .putString(getString(com.example.database.R.string.expert_system_level), expertSystemLevel.toString())
                .apply();
    }
}
