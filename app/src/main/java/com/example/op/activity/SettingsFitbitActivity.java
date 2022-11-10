package com.example.op.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.browser.customtabs.CustomTabsIntent;

import com.example.op.R;
import com.example.database.AppDatabase;
import com.example.op.http.requests.FitbitAuthorization;
import com.example.op.utils.Authorization;

import java.util.Objects;

import lombok.SneakyThrows;

public class SettingsFitbitActivity extends AppCompatActivity {

    private Authorization authorization;
    private AppDatabase database;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fitbit_settings);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);

        SwitchCompat fitbitSwitch = findViewById(R.id.switch_fitbit);

        SharedPreferences sharPref = getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        fitbitSwitch.setChecked(sharPref.getBoolean(getString(R.string.switch_state),false));

        fitbitSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!database.fitbitAccessTokenDao().getNewestAccessToken().isPresent()) {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(this, Uri.parse(authorization.getAuthorizationUrl()));
                }
                sharPref.edit().putBoolean(getString(R.string.switch_state), true).apply();
            } else {
                // TODO : disable fitbit. Some kind of block already existing token
                sharPref.edit().putBoolean(getString(R.string.switch_state), false).apply();
            }
        });
        database = AppDatabase.getDatabaseInstance(this);

        String clientId = getString(R.string.client_id);
        String clientSecret = getString(R.string.client_secret);
        String scopes = getString(R.string.scopes);
        String redirectUrl = getString(R.string.redirect_url);

        authorization = new Authorization(clientId, clientSecret, scopes, redirectUrl);

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
