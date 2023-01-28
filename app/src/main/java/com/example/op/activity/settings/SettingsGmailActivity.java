package com.example.op.activity.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.database.AppDatabase;
import com.example.database.SecureSharedPreferences;
import com.example.database.entity.Profile;
import com.example.op.R;
import com.example.op.activity.extra.GlobalSetupAppCompatActivity;

import java.util.regex.Pattern;

public class SettingsGmailActivity extends GlobalSetupAppCompatActivity {
    private static final String TAG = SettingsGmailActivity.class.getName();
    private String gmailAddress, gmailPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gmail_settings);

        String opiumPreferences = getString(com.example.database.R.string.opium_preferences);
        SharedPreferences sharPref = this.getSharedPreferences(opiumPreferences, MODE_PRIVATE);
        SharedPreferences secureSharPref = SecureSharedPreferences.get(this, opiumPreferences);
        gmailAddress = getString(com.example.database.R.string.gmail_address);
        gmailPassword = getString(com.example.database.R.string.gmail_password);

        EditText emailAddressEv = findViewById(R.id.edit_view_email_address);
        EditText emailPasswordEv = findViewById(R.id.edit_view_email_password);

        String gmailAddress = sharPref.getString(this.gmailAddress, "");
        String gmailPassword = secureSharPref.getString(this.gmailPassword, "");

        emailAddressEv.setText(gmailAddress);
        emailPasswordEv.setText(gmailPassword);

        Button applyEmailCredentialsButton = findViewById(R.id.button_apply_email_credentials);

        applyEmailCredentialsButton.setOnClickListener(v -> {
            SharedPreferences.Editor edit = sharPref.edit();
            SharedPreferences.Editor secureEdit = secureSharPref.edit();
            String emailAddress = emailAddressEv.getText().toString();
            if (!isValidGmailAddress(emailAddress)) {
                Toast.makeText(this, "Given address is not valid gmail address", Toast.LENGTH_SHORT).show();
            } else {
                String emailPassword = emailPasswordEv.getText().toString();
                AppDatabase database = AppDatabase.getInstance(getApplicationContext());
                Profile profile = database.profileDao().get().get();
                profile.setEmailAddress(emailAddress);
                database.profileDao().update(profile);

                edit.putString(this.gmailAddress, emailAddress).apply();
                secureEdit.putString(this.gmailPassword, emailPassword).apply();
                Log.i(TAG, "Email credentials changed");
                SettingsGmailActivity.this.finish();
            }
        });
    }

    public boolean isValidGmailAddress(String email) {
        String gmailValidMailRegex = "^([a-zA-Z0-9_\\-\\.]+)@gmail.([a-zA-Z]{2,5})$";
        Pattern pat = Pattern.compile(gmailValidMailRegex);
        if (email == null) {
            return false;
        }
        return pat.matcher(email).matches();
    }
}
