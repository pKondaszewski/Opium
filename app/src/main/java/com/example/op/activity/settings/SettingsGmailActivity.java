package com.example.op.activity.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.database.AppDatabase;
import com.example.database.entity.Profile;
import com.example.op.R;
import com.example.op.activity.extra.TranslatedAppCompatActivity;

public class SettingsGmailActivity extends TranslatedAppCompatActivity {

    private static final String TAG = SettingsGmailActivity.class.getName();
    private String gmailAddress, gmailPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmail_settings);

        SharedPreferences sharPref = this.getSharedPreferences(getString(R.string.opium_preferences), MODE_PRIVATE);

        gmailAddress = getString(R.string.gmail_address);
        gmailPassword = getString(R.string.gmail_password);

        Button applyEmailCredentialsButton = findViewById(R.id.applyEmailCredentialsButton);
        EditText emailAddressEv = findViewById(R.id.edit_view_email_address);
        EditText emailPasswordEv = findViewById(R.id.edit_view_email_password);

        String gmailAddress = sharPref.getString(this.gmailAddress, "");
        emailAddressEv.setText(gmailAddress);
        String gmailPassword = sharPref.getString(this.gmailPassword, "");
        emailPasswordEv.setText(gmailPassword);

        applyEmailCredentialsButton.setOnClickListener(v -> {
            SharedPreferences.Editor edit = sharPref.edit();
            String emailAddress = emailAddressEv.getText().toString();
            String emailPassword = emailPasswordEv.getText().toString();

            AppDatabase database = AppDatabase.getDatabaseInstance(getApplicationContext());
            Profile profile = database.profileDao().get().get();
            profile.setEmailAddress(emailAddress);
            database.profileDao().update(profile);

            edit.putString(this.gmailAddress, emailAddress);
            edit.putString(this.gmailPassword, emailPassword);
            edit.apply();
            Log.i(TAG, "Email credentials changed to mail: " + emailAddress + ", password: " + emailPassword);
            SettingsGmailActivity.this.finish();
        });
    }
}
