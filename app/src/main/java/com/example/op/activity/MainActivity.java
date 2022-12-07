package com.example.op.activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;

import com.example.database.AppDatabase;
import com.example.database.InitialDatabaseData;
import com.example.expertsystem.ExpertSystem;
import com.example.op.R;
import com.example.op.activity.analyze.AnalyzeActivity;
import com.example.op.activity.extra.EmergencyActivity;
import com.example.op.activity.extra.TranslatedAppCompatActivity;
import com.example.op.activity.history.TreatmentHistoryActivity;
import com.example.op.activity.profile.ProfileActivity;
import com.example.op.activity.report.ReportActivity;
import com.example.op.activity.settings.SettingsActivity;
import com.example.op.activity.user.DailyFeelingsActivity;
import com.example.op.utils.InitialSharedPreferences;
import com.example.op.utils.LocaleHelper;
import com.example.op.worker.WorkerFactory;

import java.time.LocalDate;

import lombok.SneakyThrows;

public class MainActivity extends TranslatedAppCompatActivity implements View.OnClickListener {

    private String CHANNEL_ID = "1";
    private static final String TAG = MainActivity.class.getName();
    private AppDatabase database;
    private Button dailyControlBtn;
    private OneTimeWorkRequest userActivityRangeRequest;
    private WorkerFactory workerFactory;
    private float screenX1, screenX2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannel();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        dailyControlBtn = findViewById(R.id.button_daily_feelings);
        Button treatmentHistoryBtn = findViewById(R.id.button_treatment_history);
        Button reportBtn = findViewById(R.id.button_report);
        Button analyzeBtn = findViewById(R.id.button_analyze);
        Button helpBtn = findViewById(R.id.button_help);
        Button settingsBtn = findViewById(R.id.button_settings);

        dailyControlBtn.setOnClickListener(this);
        treatmentHistoryBtn.setOnClickListener(this);
        reportBtn.setOnClickListener(this);
        analyzeBtn.setOnClickListener(this);
        helpBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);

        dailyControlBtn.startAnimation(animation);
        treatmentHistoryBtn.startAnimation(animation);
        reportBtn.startAnimation(animation);
        analyzeBtn.startAnimation(animation);
        helpBtn.startAnimation(animation);
        settingsBtn.startAnimation(animation);

        database = AppDatabase.getDatabaseInstance(this);
        InitialDatabaseData.initControlTextQuestions(database);
        InitialDatabaseData.initProfile(database);
        InitialDatabaseData.initData(database);

        InitialSharedPreferences.initStartValues(this);
        SharedPreferences sharPref = this.getSharedPreferences(getString(R.string.opium_preferences), Context.MODE_PRIVATE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }

        workerFactory = new WorkerFactory(this);
    }

    private void createNotificationChannel() {
        CharSequence name = "notificationChannel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @SneakyThrows
    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_daily_feelings) {
            Intent intent = new Intent(this, DailyFeelingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.button_treatment_history) {
            Intent intent = new Intent(this, TreatmentHistoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.button_report) {
            Intent intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
        } else if (id == R.id.button_analyze) {
            Intent intent = new Intent(this, AnalyzeActivity.class);
            startActivity(intent);
        } else if (id == R.id.button_help) {
            ExpertSystem expertSystem = new ExpertSystem(this);
        } else if (id == R.id.button_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.setLocale(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        LocaleHelper.setLocale(getApplicationContext());
        workerFactory.enqueueWorks();
        if (database.dailyFeelingsDao().getByDate(LocalDate.now()).isPresent()) {
            dailyControlBtn.setEnabled(false);
            dailyControlBtn.setText(R.string.daily_control_completed_button_name);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            screenX1 = event.getX();
            System.out.println(screenX1);
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            screenX2 = event.getX();
            System.out.println(screenX2);
            if (screenX1 - screenX2 > 0) {
                Intent intent = new Intent(this, EmergencyActivity.class);
                startActivity(intent);
            }
        }
        return false;
    }
}