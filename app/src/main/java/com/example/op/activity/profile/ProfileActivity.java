package com.example.op.activity.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.database.AppDatabase;
import com.example.database.HomeAddress;
import com.example.database.entity.Profile;
import com.example.op.R;
import com.example.op.exception.ResourceNotFoundException;
import com.example.op.utils.Translation;

public class ProfileActivity extends AppCompatActivity implements MenuItem.OnMenuItemClickListener {

    private static final String TAG = ProfileActivity.class.getName();
    private AppDatabase database;
    private Profile profile;
    private TextView nameValueTv, birthdateValueTv, sexValueTv, phoneNumberValueTv, emailAddressValueTv,
            homeAddressValueTv;
    private Translation translation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        database = AppDatabase.getDatabaseInstance(this);
        translation = new Translation(this);
        profile = database.profileDao().get().orElseThrow(
                () -> new ResourceNotFoundException(TAG, "Profile doesn't exist in database"));

        nameValueTv = findViewById(R.id.text_view_name_value);
        birthdateValueTv = findViewById(R.id.text_view_birthdate_value);
        sexValueTv = findViewById(R.id.text_view_sex_value);
        phoneNumberValueTv = findViewById(R.id.text_view_phone_number_value);
        emailAddressValueTv = findViewById(R.id.text_view_email_address_value);
        homeAddressValueTv = findViewById(R.id.text_view_home_address_value);
    }

    @Override
    public void onResume(){
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        profile = database.profileDao().get().orElseThrow(
                () -> new ResourceNotFoundException(TAG, "Profile doesn't exist in database"));

        nameValueTv.setText(String.format("%s %s", profile.getFirstname(), profile.getLastname()));
        birthdateValueTv.setText(profile.getBirthdate().toString());
        sexValueTv.setText(translation.translateSex(profile.getSex()));
        phoneNumberValueTv.setText(profile.getPhoneNumber());
        emailAddressValueTv.setText(profile.getEmailAddress());
        homeAddressValueTv.setText(extractHomeAddress(profile.getHomeAddress()));
    }

    private String extractHomeAddress(HomeAddress homeAddress) {
        return homeAddress == null ? "" : homeAddress.toSimpleString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        for (int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setOnMenuItemClickListener(this);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId() == R.id.editProfileMenuItem) {
            Intent intent = new Intent(this, EditProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }
}
