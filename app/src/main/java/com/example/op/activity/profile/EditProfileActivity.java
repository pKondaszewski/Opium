package com.example.op.activity.profile;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.database.HomeAddress;
import com.example.op.R;
import com.example.database.AppDatabase;
import com.example.database.entity.Profile;
import com.example.op.exception.ResourceNotFoundException;

import java.time.LocalDate;

public class EditProfileActivity extends AppCompatActivity {

    private static final String TAG = EditProfileActivity.class.getName();
    private AppDatabase database;
    private DatePicker birthdateValueDp;
    private EditText firstnameValueEt, lastnameValueEt, sexValueEt, phoneNumberValueEt,
            emailAddressValueEt, streetAddressValueEt, postalCodeValueEt, countryNameValueEt;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        database = AppDatabase.getDatabaseInstance(this);
        profile = database.profileDao().get().orElseThrow(
                () -> new ResourceNotFoundException(TAG, "Profile doesn't exist in database"));

        firstnameValueEt = findViewById(R.id.edit_text_firstname_value);
        lastnameValueEt = findViewById(R.id.edit_text_lastname_value);
        birthdateValueDp = findViewById(R.id.date_picker_birthdate_value);
        sexValueEt = findViewById(R.id.edit_text_sex_value);
        phoneNumberValueEt = findViewById(R.id.edit_text_phone_number_value);
        emailAddressValueEt = findViewById(R.id.edit_text_email_address_value);
        streetAddressValueEt = findViewById(R.id.edit_text_street_address_value);
        postalCodeValueEt = findViewById(R.id.edit_text_postal_code_value);
        countryNameValueEt = findViewById(R.id.edit_text_country_name_value);

        firstnameValueEt.setText(profile.getFirstname());
        lastnameValueEt.setText(profile.getLastname());
        LocalDate birthdate = profile.getBirthdate();
        birthdateValueDp.init(birthdate.getYear(), birthdate.getMonthValue()-1, birthdate.getDayOfMonth(), null);
        sexValueEt.setText(profile.getSex());
        phoneNumberValueEt.setText(profile.getPhoneNumber());
        emailAddressValueEt.setText(profile.getEmailAddress());
        HomeAddress homeAddress = profile.getHomeAddress();
        streetAddressValueEt.setText(homeAddress == null ? "" : homeAddress.getStreetAddress());
        postalCodeValueEt.setText(homeAddress == null ? "" : homeAddress.getPostalCode());
        countryNameValueEt.setText(homeAddress == null ? "" : homeAddress.getCountry());
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
        String sex = sexValueEt.getText().toString();
        String emailAddress = emailAddressValueEt.getText().toString();
        String streetAddress = streetAddressValueEt.getText().toString();
        String postalCode = postalCodeValueEt.getText().toString();
        String countryName = countryNameValueEt.getText().toString();
        HomeAddress homeAddress = new HomeAddress(streetAddress, postalCode, countryName);

        profile = new Profile(profileId, firstname, lastname, birthdate, phoneNumber, sex, emailAddress, homeAddress);
        database.profileDao().update(profile);
        Log.i(TAG, "Profile updated: " + profile);

        SharedPreferences.Editor edit = getSharedPreferences(getString(R.string.opium_preferences), MODE_PRIVATE).edit();
        edit.putString(getString(R.string.gmail_address), emailAddress).apply();
        Log.i(TAG, "Shared preferences email updated to: " + emailAddress);
    }
}
