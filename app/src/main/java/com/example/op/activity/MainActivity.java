package com.example.op.activity;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.op.R;
import com.example.op.database.AppDatabase;
import com.example.op.database.InitialDatabaseData;
import com.example.op.database.entity.FitbitAccessToken;
import com.example.op.exception.UnauthorizedFitbitException;
import com.example.op.http.HttpClient;
import com.example.op.http.requests.FitbitRequest;
import com.example.op.service.FitbitDataService;
import com.example.op.service.LocalizationService;
import com.example.op.utils.Authorization;
import com.example.op.utils.FitbitUrlBuilder;
import com.google.android.gms.location.FusedLocationProviderClient;

import java.time.LocalDate;

import okhttp3.OkHttpClient;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    FitbitAccessToken fitbitAccessToken;
    Button authorizationButton, refreshButton;
    TextView textView;
    private String clientId, clientSecret, scopes, redirectUrl;
    float x, y, z, a, b, c;
    float mSteps = 0;//步数
    float tempSteps = 0;//步数
    int licznik = 0;

    float screenX1, screenX2;

    AppDatabase database;

    String CHANNEL_ID = "1";

    private static final String TAG = "MyActivity";
    private FusedLocationProviderClient fusedLocationClient;

    Button dailyControlButton, treatmentHistoryButton, reportButton, analyzeButton, remindersButton, settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //this.deleteDatabase("DB_NAME");

        //createNotificationChannel();

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fadein);

        dailyControlButton = (Button) findViewById(R.id.dailyFeelingsButton);
        dailyControlButton.setOnClickListener(this);
        dailyControlButton.startAnimation(animation);

        treatmentHistoryButton = (Button) findViewById(R.id.treatmentHistoryButton);
        treatmentHistoryButton.setOnClickListener(this);
        treatmentHistoryButton.startAnimation(animation);

        reportButton = (Button) findViewById(R.id.reportButton);
        reportButton.setOnClickListener(this);
        reportButton.startAnimation(animation);

        analyzeButton = (Button) findViewById(R.id.analyzeButton);
        analyzeButton.setOnClickListener(this);
        analyzeButton.startAnimation(animation);

        remindersButton = (Button) findViewById(R.id.remindersButton);
        remindersButton.setOnClickListener(this);
        remindersButton.startAnimation(animation);



        settingsButton = (Button) findViewById(R.id.settingsButton);
        settingsButton.setOnClickListener(this);
        settingsButton.startAnimation(animation);

        database = AppDatabase.getDatabaseInstance(this);
        InitialDatabaseData.initControlTextQuestions(database);
        System.out.println(database.controlTextQuestionDao().getAll());
//
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
//            return;
//        }
//        Intent intent = new Intent(this, LocalizationService.class);
//        startService(intent);
//
//        Intent intent2 = new Intent(this, FitbitDataService.class);
//        startService(intent2);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        CharSequence name = "channelName";
        String description = "channelDescription";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dailyFeelingsButton) {
            Intent intent = new Intent(this, DailyFeelingsActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.treatmentHistoryButton) {
            Intent intent = new Intent(this, TreatmentHistoryActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.reportButton) {
            Intent intent = new Intent(this, ReportActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.analyzeButton) {
            Intent intent = new Intent(this, AnalyzeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.remindersButton) {
            String clientId = getString(R.string.clientId);
            String clientSecret = getString(R.string.clientSecret);
            String scopes = getString(R.string.scopes);
            String redirectUrl = getString(R.string.redirectUrl);
            Authorization authorization = new Authorization(clientId, clientSecret, scopes, redirectUrl);

            FitbitAccessToken fitbitAccessToken = database.fitbitAccessTokenDao().getNewestAccessToken().orElseThrow(
                    () -> new UnauthorizedFitbitException(TAG, "There are no fitbit access token. Please authorize fitbit in settings activity.")
            );

            String accessToken = fitbitAccessToken.getAccessToken();
            String refreshToken = fitbitAccessToken.getRefreshToken();
            String authorizationString = authorization.getAuthorizationToken();

            OkHttpClient httpClient = HttpClient.getHttpClient(
                    getString(R.string.tokenUrl), authorizationString, refreshToken, this
            );

            AppDatabase database = AppDatabase.getDatabaseInstance(this);
            FitbitRequest fitbitRequest = new FitbitRequest(accessToken, httpClient, database);
            FitbitRequest fitbitRequest1 = new FitbitRequest(accessToken, httpClient, database);

            try {
                fitbitRequest.getStepsByDay(FitbitUrlBuilder.stepsUrl(LocalDate.now()));
                fitbitRequest1.getSpO2ByDay(FitbitUrlBuilder.spO2Url(LocalDate.of(2022,7,5)));
            } catch (InterruptedException exception) {
                exception.printStackTrace();
            }
        } else if (v.getId() == R.id.settingsButton) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (database.dailyFeelingsDao().getByDate(LocalDate.now()).isPresent()) {
            dailyControlButton.setEnabled(false);
            dailyControlButton.setText(R.string.daily_control_completed_button_name);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.profilMenuItem) {
            Toast.makeText(this, "Pokazanie profilu", Toast.LENGTH_SHORT).show();
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