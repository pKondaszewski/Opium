package com.example.op.activity.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.database.AppDatabase;
import com.example.database.entity.Profile;
import com.example.op.R;
import com.example.op.activity.extra.GlobalSetupAppCompatActivity;

public class SettingsGmailActivity extends GlobalSetupAppCompatActivity {
    private static final String TAG = SettingsGmailActivity.class.getName();
    private String gmailAddress, gmailPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmail_settings);

        SharedPreferences sharPref = this.getSharedPreferences(getString(com.example.database.R.string.opium_preferences), MODE_PRIVATE);
        gmailAddress = getString(com.example.database.R.string.gmail_address);
        gmailPassword = getString(com.example.database.R.string.gmail_password);

        EditText emailAddressEv = findViewById(R.id.edit_view_email_address);
        EditText emailPasswordEv = findViewById(R.id.edit_view_email_password);

        String gmailAddress = sharPref.getString(this.gmailAddress, "");
        String gmailPassword = sharPref.getString(this.gmailPassword, "");

        emailAddressEv.setText(gmailAddress);
        emailPasswordEv.setText(gmailPassword);

        Button applyEmailCredentialsButton = findViewById(R.id.button_apply_email_credentials);

        applyEmailCredentialsButton.setOnClickListener(v -> {
            SharedPreferences.Editor edit = sharPref.edit();
            String emailAddress = emailAddressEv.getText().toString();
            String emailPassword = emailPasswordEv.getText().toString();

            AppDatabase database = AppDatabase.getDatabaseInstance(getApplicationContext());
            Profile profile = database.profileDao().get().get();
            profile.setEmailAddress(emailAddress);
            database.profileDao().update(profile);

            edit.putString(this.gmailAddress, emailAddress)
                .putString(this.gmailPassword, emailPassword)
                .apply();
            Log.i(TAG, "Email credentials changed to mail: " + emailAddress + ", password: " + emailPassword);
            SettingsGmailActivity.this.finish();
        });
    }
}
