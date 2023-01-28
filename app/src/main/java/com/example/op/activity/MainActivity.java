package com.example.op.activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
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

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.work.ExistingPeriodicWorkPolicy;

import com.example.database.AppDatabase;
import com.example.database.InitialDatabaseData;
import com.example.database.entity.Profile;
import com.example.op.R;
import com.example.op.activity.analyze.AnalyzeActivity;
import com.example.op.activity.extra.EmergencyActivity;
import com.example.op.activity.extra.GlobalSetupAppCompatActivity;
import com.example.op.activity.history.TreatmentHistoryActivity;
import com.example.op.activity.profile.ProfileActivity;
import com.example.op.activity.report.ReportActivity;
import com.example.op.activity.settings.SettingsActivity;
import com.example.op.activity.user.DailyFeelingsActivity;
import com.example.op.service.NotificationService;
import com.example.op.utils.LocaleHelper;
import com.example.op.utils.Start;
import com.example.op.worker.WorkerFactory;

import java.time.LocalDate;
import java.util.Objects;

public class MainActivity extends GlobalSetupAppCompatActivity implements View.OnClickListener {
    private static final String TAG = MainActivity.class.getName();
    private AppDatabase database;
    private Button dailyFeelingsBtn;
    private WorkerFactory workerFactory;
    private float screenX1, screenX2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = AppDatabase.getInstance(this);
        database.dailyQuestionDao().getAll().forEach(System.out::println);
        createNotificationChannel();

        dailyFeelingsBtn = findViewById(R.id.button_daily_feelings);
        Button treatmentHistoryBtn = findViewById(R.id.button_treatment_history);
        Button reportBtn = findViewById(R.id.button_report);
        Button analyzeBtn = findViewById(R.id.button_analyze);
        Button settingsBtn = findViewById(R.id.button_settings);

        dailyFeelingsBtn.setOnClickListener(this);
        treatmentHistoryBtn.setOnClickListener(this);
        reportBtn.setOnClickListener(this);
        analyzeBtn.setOnClickListener(this);
        settingsBtn.setOnClickListener(this);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadein);
        dailyFeelingsBtn.startAnimation(animation);
        treatmentHistoryBtn.startAnimation(animation);
        reportBtn.startAnimation(animation);
        analyzeBtn.startAnimation(animation);
        settingsBtn.startAnimation(animation);

        SharedPreferences sharPref = getSharedPreferences(getString(com.example.database.R.string.opium_preferences), MODE_PRIVATE);
        String isRepeatable = sharPref.getString(getString(com.example.database.R.string.is_repeatable), "false");
        if (Boolean.parseBoolean(isRepeatable)) {
            Start.service(this, NotificationService.class);
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
        }

        workerFactory = new WorkerFactory(this);
    }

    private void createNotificationChannel() {
        String CHANNEL_ID = "1";
        CharSequence name = "notificationChannel";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    private void setupDailyFeelingButtonState() {
        if (database.dailyFeelingsDao().getByDate(LocalDate.now()).isPresent()) {
            dailyFeelingsBtn.setEnabled(false);
            dailyFeelingsBtn.setText(R.string.daily_feelings_completed_button_name);
        }
    }

    private void setupHelloTitle() {
        String helloLabel = getString(R.string.hello_label);
        Profile profile = database.profileDao().get().orElse(new Profile());
        String firstname = profile.getFirstname();
        if (firstname != null && !firstname.equals("")) {
            Objects.requireNonNull(getSupportActionBar())
                    .setTitle(helloLabel + ", " + firstname);
        } else {
            Objects.requireNonNull(getSupportActionBar())
                    .setTitle(helloLabel);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_daily_feelings) {
            Start.activity(this, DailyFeelingsActivity.class);
        } else if (id == R.id.button_treatment_history) {
            Start.activity(this, TreatmentHistoryActivity.class);
        } else if (id == R.id.button_report) {
            Start.activity(this, ReportActivity.class);
        } else if (id == R.id.button_analyze) {
            Start.activity(this, AnalyzeActivity.class);
        } else if (id == R.id.button_settings) {
            Start.activity(this, SettingsActivity.class);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LocaleHelper.setLocale(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        LocaleHelper.setLocale(getApplicationContext());
        workerFactory.enqueueWorks(ExistingPeriodicWorkPolicy.KEEP);
        setupDailyFeelingButtonState();
        setupHelloTitle();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_item_profile) {
            Start.activity(this, ProfileActivity.class);
            return true;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            screenX1 = event.getX();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            screenX2 = event.getX();
            if (screenX1 - screenX2 > 0) {
                Start.activity(this, EmergencyActivity.class);
            }
        }
        return false;
    }
}