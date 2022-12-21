package com.example.op.activity.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.TransactionTooLargeException;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.database.HomeAddress;
import com.example.op.R;
import com.example.database.AppDatabase;
import com.example.database.entity.Profile;
import com.example.op.activity.extra.TranslatedAppCompatActivity;
import com.example.op.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.util.Map;

public class EditProfileActivity extends TranslatedAppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final String TAG = EditProfileActivity.class.getName();
    private AppDatabase database;
    private DatePicker birthdateValueDp;
    private EditText firstnameValueEt, lastnameValueEt, phoneNumberValueEt,
            emailAddressValueEt, streetAddressValueEt, postalCodeValueEt, countryNameValueEt;
    private SharedPreferences sharPref;
    private Spinner sexValueSp;
    private String sex;
    private Profile profile;

    private final String[] sexes = {"Male", "Female", "Other"};
    private String[] translatedSexes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        translatedSexes = new String[]{ getString(R.string.male_sex_spinner_item),
                                        getString(R.string.female_sex_spinner_item),
                                        getString(R.string.other_sex_spinner_item)};

        database = AppDatabase.getDatabaseInstance(this);
        sharPref = getSharedPreferences(getString(com.example.database.R.string.opium_preferences), MODE_PRIVATE);
        profile = database.profileDao().get().orElseThrow(
                () -> new ResourceNotFoundException(TAG, "Profile doesn't exist in database"));

        firstnameValueEt = findViewById(R.id.edit_text_firstname_value);
        lastnameValueEt = findViewById(R.id.edit_text_lastname_value);
        birthdateValueDp = findViewById(R.id.date_picker_birthdate_value);
        sexValueSp = findViewById(R.id.spinner_sex);
        phoneNumberValueEt = findViewById(R.id.edit_text_phone_number_value);
        emailAddressValueEt = findViewById(R.id.edit_text_email_address_value);
        streetAddressValueEt = findViewById(R.id.edit_text_street_address_value);
        postalCodeValueEt = findViewById(R.id.edit_text_postal_code_value);
        countryNameValueEt = findViewById(R.id.edit_text_country_name_value);

        firstnameValueEt.setText(profile.getFirstname());
        lastnameValueEt.setText(profile.getLastname());
        LocalDate birthdate = profile.getBirthdate();
        birthdateValueDp.init(birthdate.getYear(), birthdate.getMonthValue()-1, birthdate.getDayOfMonth(), null);
        setupSexSpinner();
        phoneNumberValueEt.setText(profile.getPhoneNumber());
        emailAddressValueEt.setText(profile.getEmailAddress());
        HomeAddress homeAddress = profile.getHomeAddress();
        streetAddressValueEt.setText(homeAddress == null ? "" : homeAddress.getStreetAddress());
        postalCodeValueEt.setText(homeAddress == null ? "" : homeAddress.getPostalCode());
        countryNameValueEt.setText(homeAddress == null ? "" : homeAddress.getCountry());
    }

    private void setupSexSpinner() {
        ArrayAdapter translatedAd = new ArrayAdapter(this, android.R.layout.simple_spinner_item, translatedSexes);
        sexValueSp.setAdapter(translatedAd);
        sexValueSp.setOnItemSelectedListener(this);

        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, sexes);
        int selectionPosition = ad.getPosition(profile.getSex());
        sexValueSp.setSelection(selectionPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        int profileId = profile.getId();

        String firstname = firstnameValueEt.getText().toString();
        String lastname = lastnameValueEt.getText().toString();
        LocalDate birthdate = LocalDate.of(birthdateValueDp.getYear(),
                birthdateValueDp.getMonth()+1,
                birthdateValueDp.getDayOfMonth());
        String phoneNumber = phoneNumberValueEt.getText().toString();
        String emailAddress = emailAddressValueEt.getText().toString();
        sharPref.edit().putString(getString(com.example.database.R.string.gmail_address), emailAddress).apply();
        String streetAddress = streetAddressValueEt.getText().toString();
        String postalCode = postalCodeValueEt.getText().toString();
        String countryName = countryNameValueEt.getText().toString();
        HomeAddress homeAddress = new HomeAddress(streetAddress, postalCode, countryName);

        profile = new Profile(profileId, firstname, lastname, birthdate, sex, phoneNumber, emailAddress, homeAddress);
        database.profileDao().update(profile);
        Log.i(TAG, "Profile updated: " + profile);

        SharedPreferences.Editor edit = getSharedPreferences(getString(com.example.database.R.string.opium_preferences), MODE_PRIVATE).edit();
        edit.putString(getString(com.example.database.R.string.gmail_address), emailAddress).apply();

        Log.i(TAG, "Shared preferences email updated to: " + emailAddress);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0 -> sex = "Male";
            case 1 -> sex = "Female";
            case 2 -> sex = "Other";
        }
        sexValueSp.setSelection(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}
