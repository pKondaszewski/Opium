package com.example.op.activity.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.fragment.app.DialogFragment;

import com.example.op.R;
import com.example.op.activity.extra.TranslatedAppCompatActivity;
import com.example.op.fragment.settings.AppLanguageDialogFragment;

public class SettingsActivity extends TranslatedAppCompatActivity implements View.OnClickListener {

    private static final String TAG = SettingsActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Button fitbitSettingsBtn = findViewById(R.id.button_fitbit_settings);
        Button gmailSettingsBtn = findViewById(R.id.button_gmail_settings);
        Button appLanguageBtn = findViewById(R.id.button_app_language);

        fitbitSettingsBtn.setOnClickListener(this);
        gmailSettingsBtn.setOnClickListener(this);
        appLanguageBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_fitbit_settings) {
            Intent intent = new Intent(this, SettingsFitbitActivity.class);
            startActivity(intent);
        } else if (id == R.id.button_gmail_settings) {
            Intent intent = new Intent(this, SettingsGmailActivity.class);
            startActivity(intent);
        } else if (id == R.id.button_app_language) {
            DialogFragment newFragment = new AppLanguageDialogFragment();
            newFragment.show(getSupportFragmentManager(), TAG);
        }
    }
}
