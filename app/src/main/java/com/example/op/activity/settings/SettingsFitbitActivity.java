package com.example.op.activity.settings;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.browser.customtabs.CustomTabsIntent;

import com.example.database.AppDatabase;
import com.example.op.R;
import com.example.op.http.requests.FitbitAuthorization;
import com.example.op.utils.Authorization;

import java.util.Map;
import java.util.Objects;

public class SettingsFitbitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitbit_settings);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

        AppDatabase database = AppDatabase.getDatabaseInstance(this);
        SharedPreferences sharPref = getSharedPreferences(getString(com.example.database.R.string.opium_preferences), Context.MODE_PRIVATE);

        String clientId = getString(R.string.client_id);
        String clientSecret = getString(R.string.client_secret);
        String scopes = getString(R.string.scopes);
        String redirectUrl = getString(R.string.redirect_url);
        Authorization authorization = new Authorization(clientId, clientSecret, scopes, redirectUrl);

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
        Map<String, ?> all = sharPref.getAll();
        System.out.println(all);
        Intent intent = getIntent();
        if (Objects.equals(intent.getAction(), Intent.ACTION_VIEW)) {
            Uri data = intent.getData();
            String code = data.toString().substring(30, 70);
            String tokenUrl = getString(R.string.token_url);
            String authorizationString = authorization.getAuthorizationToken();

            FitbitAuthorization.exchangeCodeForAccessToken(code, redirectUrl, tokenUrl, authorizationString, database);
        }
    }
}
