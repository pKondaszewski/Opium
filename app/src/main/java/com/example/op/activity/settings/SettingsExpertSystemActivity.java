package com.example.op.activity.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.expertsystem.ExpertSystemLevel;
import com.example.op.R;
import com.example.op.activity.extra.TranslatedAppCompatActivity;
import com.example.op.exception.ResourceNotFoundException;
import com.example.op.utils.simple.SimpleOnSeekBarChangeListener;

import java.util.Map;

public class SettingsExpertSystemActivity extends TranslatedAppCompatActivity {

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
                ExpertSystemLevel expertSystemLevel = getExpertSystemLevelByValue(progress);
                expertSystemLevelTv.setText(expertSystemLevel.toString());
            }
        });
    }

    private ExpertSystemLevel getExpertSystemLevelByValue(int value) {
        return switch (value) {
            case 0 -> ExpertSystemLevel.LOW;
            case 1 -> ExpertSystemLevel.MEDIUM;
            case 2 -> ExpertSystemLevel.HIGH;
            default -> throw new ResourceNotFoundException(TAG, "Value from expert system settings seekBar is not 0, 1 or 2.");
        };
    }

    private int getExpertSystemValueByLevel(ExpertSystemLevel level) {
        return switch (level) {
            case LOW -> 0;
            case MEDIUM -> 1;
            case HIGH -> 2;
        };
    }

    @Override
    protected void onPause() {
        super.onPause();
        ExpertSystemLevel expertSystemLevel = getExpertSystemLevelByValue(expertSystemLevelSb.getProgress());
        sharPref.edit()
                .putString(getString(com.example.database.R.string.expert_system_level), expertSystemLevel.toString())
                .apply();
    }
}
