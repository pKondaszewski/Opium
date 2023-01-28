package com.example.op.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.browser.customtabs.CustomTabsIntent;

import com.example.database.AppDatabase;
import com.example.op.R;
import com.example.op.activity.extra.GlobalSetupAppCompatActivity;
import com.example.op.http.requests.FitbitAuthorization;
import com.example.op.utils.Authorization;

import java.util.Objects;

public class SettingsFitbitActivity extends GlobalSetupAppCompatActivity {
    private AppDatabase database;
    private Authorization authorization;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitbit_settings);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

        database = AppDatabase.getInstance(this);
        SharedPreferences sharPref = getSharedPreferences(getString(com.example.database.R.string.opium_preferences), Context.MODE_PRIVATE);

        String clientId = getString(R.string.client_id);
        String clientSecret = getString(R.string.client_secret);
        String scopes = getString(R.string.scopes);
        String redirectUrl = getString(R.string.redirect_url);
        authorization = new Authorization(clientId, clientSecret, scopes, redirectUrl);

        SwitchCompat fitbitSwitch = findViewById(R.id.switch_fitbit);
        String fitbitSwitchState = sharPref.getString(getString(com.example.database.R.string.fitbit_switch_state), "false");
        fitbitSwitch.setChecked(Boolean.parseBoolean(fitbitSwitchState));
        fitbitSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!database.fitbitAccessTokenDao().getNewestAccessToken().isPresent()) {
                    CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder().build();
                    customTabsIntent.launchUrl(this, Uri.parse(authorization.getAuthorizationUrl()));
                }
                sharPref.edit().putString(getString(com.example.database.R.string.fitbit_switch_state), "true").apply();
            } else {
                sharPref.edit().putString(getString(com.example.database.R.string.fitbit_switch_state), "false").apply();
            }
        });
        Intent intent = getIntent();
        if (Objects.equals(intent.getAction(), Intent.ACTION_VIEW)) {
            handleFitbitRedirection(intent, redirectUrl);
        }
    }

    private void handleFitbitRedirection(Intent intent, String redirectUrl) {
        Uri data = intent.getData();
        String code = data.toString().substring(25, 65);
        String tokenUrl = getString(R.string.token_url);
        String authorizationString = authorization.getAuthorizationToken();
        FitbitAuthorization.exchangeCodeForAccessToken(code, redirectUrl, tokenUrl, authorizationString, database);
    }
}
