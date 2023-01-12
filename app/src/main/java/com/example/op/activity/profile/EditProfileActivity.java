package com.example.op.activity.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.database.AppDatabase;
import com.example.database.HomeAddress;
import com.example.database.entity.Profile;
import com.example.op.R;
import com.example.op.activity.extra.GlobalSetupAppCompatActivity;

import java.time.LocalDate;

public class EditProfileActivity extends GlobalSetupAppCompatActivity implements AdapterView.OnItemSelectedListener {
    private static final String TAG = EditProfileActivity.class.getName();
    private final String[] genders = {"Male", "Female", "Other"};
    private AppDatabase database;
    private DatePicker birthdateValueDp;
    private EditText firstnameValueEt, lastnameValueEt, phoneNumberValueEt,
            emailAddressValueEt, streetAddressValueEt, postalCodeValueEt, countryNameValueEt;
    private SharedPreferences sharPref;
    private Spinner genderValueSp;
    private String gender;
    private Profile profile;
    private String[] translatedGenders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        translatedGenders = new String[]{ getString(R.string.male_gender_spinner_item),
                                        getString(R.string.female_gender_spinner_item),
                                        getString(R.string.other_gender_spinner_item)};

        database = AppDatabase.getDatabaseInstance(this);
        sharPref = getSharedPreferences(getString(com.example.database.R.string.opium_preferences), MODE_PRIVATE);
        profile = database.profileDao().get().orElse(new Profile());

        firstnameValueEt = findViewById(R.id.edit_text_firstname_value);
        lastnameValueEt = findViewById(R.id.edit_text_lastname_value);
        birthdateValueDp = findViewById(R.id.date_picker_birthdate_value);
        genderValueSp = findViewById(R.id.spinner_gender);
        phoneNumberValueEt = findViewById(R.id.edit_text_phone_number_value);
        emailAddressValueEt = findViewById(R.id.edit_text_email_address_value);
        streetAddressValueEt = findViewById(R.id.edit_text_street_address_value);
        postalCodeValueEt = findViewById(R.id.edit_text_postal_code_value);
        countryNameValueEt = findViewById(R.id.edit_text_country_name_value);

        setupEditTexts();
    }

    private void setupEditTexts() {
        firstnameValueEt.setText(profile.getFirstname());
        lastnameValueEt.setText(profile.getLastname());
        LocalDate birthdate = profile.getBirthdate();
        birthdateValueDp.init(birthdate.getYear(), birthdate.getMonthValue()-1, birthdate.getDayOfMonth(), null);
        setupGenderSpinner();
        phoneNumberValueEt.setText(profile.getPhoneNumber());
        emailAddressValueEt.setText(profile.getEmailAddress());
        HomeAddress homeAddress = profile.getHomeAddress();
        streetAddressValueEt.setText(homeAddress == null ? "" : homeAddress.getStreetAddress());
        postalCodeValueEt.setText(homeAddress == null ? "" : homeAddress.getPostalCode());
        countryNameValueEt.setText(homeAddress == null ? "" : homeAddress.getCountry());
    }

    private void setupGenderSpinner() {
        ArrayAdapter<String> translatedAd = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, translatedGenders);
        genderValueSp.setAdapter(translatedAd);
        genderValueSp.setOnItemSelectedListener(this);

        ArrayAdapter<String> ad = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genders);
        int selectionPosition = ad.getPosition(profile.getGender());
        genderValueSp.setSelection(selectionPosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        int profileId = profile.getId();

        String firstname = firstnameValueEt.getText().toString().trim();
        String lastname = lastnameValueEt.getText().toString().trim();
        LocalDate birthdate = LocalDate.of(birthdateValueDp.getYear(),
                birthdateValueDp.getMonth()+1,
                birthdateValueDp.getDayOfMonth());
        String phoneNumber = phoneNumberValueEt.getText().toString().trim();
        String emailAddress = emailAddressValueEt.getText().toString().trim();
        sharPref.edit().putString(getString(com.example.database.R.string.gmail_address), emailAddress).apply();
        String streetAddress = streetAddressValueEt.getText().toString().trim();
        String postalCode = postalCodeValueEt.getText().toString().trim();
        String countryName = countryNameValueEt.getText().toString().trim();
        HomeAddress homeAddress = new HomeAddress(streetAddress, postalCode, countryName);

        profile = new Profile(profileId, firstname, lastname, birthdate, gender, phoneNumber, emailAddress, homeAddress);
        database.profileDao().update(profile);
        Log.i(TAG, "Profile updated: " + profile);

        SharedPreferences.Editor edit = getSharedPreferences(getString(com.example.database.R.string.opium_preferences), MODE_PRIVATE).edit();
        edit.putString(getString(com.example.database.R.string.gmail_address), emailAddress).apply();

        Log.i(TAG, "Shared preferences email updated to: " + emailAddress);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                gender = "Male";
                break;
            case 1:
                gender = "Female";
                break;
            case 2:
                gender = "Other";
                break;
        }
        genderValueSp.setSelection(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
}
