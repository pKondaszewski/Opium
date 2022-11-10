package com.example.op.activity.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.example.op.R;
import com.example.op.activity.TranslatedAppCompatActivity;

public class SettingsGmailActivity extends TranslatedAppCompatActivity {

    private static final String TAG = SettingsGmailActivity.class.getName();
    private static final String GMAIL_ADDRESS = "gmail_address";
    private static final String GMAIL_PASSWORD = "gmail_password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmail_settings);

        SharedPreferences sharPref = this.getSharedPreferences(getString(R.string.opium_preferences), MODE_PRIVATE);

        Button applyEmailCredentialsButton = findViewById(R.id.applyEmailCredentialsButton);
        EditText emailAddressEv = findViewById(R.id.edit_view_email_address);
        EditText emailPasswordEv = findViewById(R.id.edit_view_email_password);

        String gmailAddress = sharPref.getString(GMAIL_ADDRESS, "");
        emailAddressEv.setText(gmailAddress);
        String gmailPassword = sharPref.getString(GMAIL_PASSWORD, "");
        emailPasswordEv.setText(gmailPassword);

        applyEmailCredentialsButton.setOnClickListener(v -> {
            SharedPreferences.Editor edit = sharPref.edit();
            String emailAddress = emailAddressEv.getText().toString();
            String emailPassword = emailPasswordEv.getText().toString();
            edit.putString(GMAIL_ADDRESS, emailAddress);
            edit.putString(GMAIL_PASSWORD, emailPassword);
            edit.apply();
            Log.i(TAG, "Email credentials changed to mail: " + emailAddress + ", password: " + emailPassword);
            SettingsGmailActivity.this.finish();
        });
    }
}
