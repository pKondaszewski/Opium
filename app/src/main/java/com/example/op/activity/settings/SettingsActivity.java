package com.example.op.activity.settings;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.example.op.R;
import com.example.op.activity.extra.GlobalSetupAppCompatActivity;
import com.example.op.fragment.settings.AppLanguageDialogFragment;
import com.example.op.utils.Start;

public class SettingsActivity extends GlobalSetupAppCompatActivity implements View.OnClickListener {
    private static final String TAG = SettingsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button fitbitSettingsBtn = findViewById(R.id.button_fitbit_settings);
        Button gmailSettingsBtn = findViewById(R.id.button_gmail_settings);
        Button appLanguageBtn = findViewById(R.id.button_app_language);
        Button expertSystemBtn = findViewById(R.id.button_expert_system_settings);
        Button notificationScheduleBtn = findViewById(R.id.button_notification_schedule_settings);
        Button infoAboutAuthorSettingsBtn = findViewById(R.id.button_info_about_author_settings);

        fitbitSettingsBtn.setOnClickListener(this);
        gmailSettingsBtn.setOnClickListener(this);
        appLanguageBtn.setOnClickListener(this);
        expertSystemBtn.setOnClickListener(this);
        notificationScheduleBtn.setOnClickListener(this);
        infoAboutAuthorSettingsBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_fitbit_settings) {
            Start.activity(this, SettingsFitbitActivity.class);
        } else if (id == R.id.button_gmail_settings) {
            Start.activity(this, SettingsGmailActivity.class);
        } else if (id == R.id.button_app_language) {
            DialogFragment newFragment = new AppLanguageDialogFragment();
            newFragment.show(getSupportFragmentManager(), TAG);
        } else if (id == R.id.button_expert_system_settings) {
            Start.activity(this, SettingsExpertSystemActivity.class);
        } else if (id == R.id.button_notification_schedule_settings) {
            Start.activity(this, SettingsNotificationScheduleActivity.class);
        } else if (id == R.id.button_info_about_author_settings) {
            Start.activity(this, SettingsInfoAboutAuthorActivity.class);
        }
    }
}
