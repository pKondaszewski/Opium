package com.example.op.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.browser.customtabs.CustomTabsIntent;

import com.example.op.R;
import com.example.op.database.AppDatabase;
import com.example.op.http.requests.FitbitAuthorization;
import com.example.op.utils.Authorization;

import java.util.Objects;

import lombok.SneakyThrows;

public class SettingsActivity extends AppCompatActivity {

    private Authorization authorization;
    private AppDatabase database;
    private Switch fitbitSwitch;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fitbitSwitch = (Switch) findViewById(R.id.fitbitSwitch);

        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        fitbitSwitch.setChecked(sharedPreferences.getBoolean(getString(R.string.switchState),false));
        SharedPreferences.Editor editor = sharedPreferences.edit();

        fitbitSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!database.fitbitAccessTokenDao().getNewestAccessToken().isPresent()) {
                    CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                    CustomTabsIntent customTabsIntent = builder.build();
                    customTabsIntent.launchUrl(this, Uri.parse(authorization.getAuthorizationUrl()));
                }
                editor.putBoolean(getString(R.string.switchState), true);
            } else {
                // TODO : disable fitbit. Some kind of block already existing token
                editor.putBoolean(getString(R.string.switchState), false);
            }
            editor.apply();
        });

        database = AppDatabase.getDatabaseInstance(this);

        String clientId = getString(R.string.clientId);
        String clientSecret = getString(R.string.clientSecret);
        String scopes = getString(R.string.scopes);
        String redirectUrl = getString(R.string.redirectUrl);

        authorization = new Authorization(clientId, clientSecret, scopes, redirectUrl);

        Intent intent = getIntent();
        if (Objects.equals(intent.getAction(), Intent.ACTION_VIEW)) {
            Uri data = intent.getData();
            String code = data.toString().substring(30, 70);
            String tokenUrl = getString(R.string.tokenUrl);
            String authorizationString = authorization.getAuthorizationToken();

            FitbitAuthorization.exchangeCodeForAccessToken(code, redirectUrl, tokenUrl, authorizationString, database);
        }
    }
}
