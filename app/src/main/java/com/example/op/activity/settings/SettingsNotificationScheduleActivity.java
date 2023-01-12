package com.example.op.activity.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TimePicker;

import com.example.op.R;
import com.example.op.activity.extra.GlobalSetupAppCompatActivity;
import com.example.op.worker.WorkerFactory;

public class SettingsNotificationScheduleActivity extends GlobalSetupAppCompatActivity {
    private static final String TAG = SettingsNotificationScheduleActivity.class.getName();
    private SharedPreferences sharPref;
    private TimePicker dailyQuestionTp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_schedule_settings);

        sharPref = getSharedPreferences(getString(com.example.database.R.string.opium_preferences), MODE_PRIVATE);

        dailyQuestionTp = findViewById(R.id.time_picker_daily_question);
        dailyQuestionTp.setIs24HourView(true);

        String dailyQuestionTime = sharPref.getString(getString(com.example.database.R.string.daily_question_time), "12:00");
        String[] dailyQuestionTimeSplit = dailyQuestionTime.split(":");
        dailyQuestionTp.setHour(Integer.parseInt(dailyQuestionTimeSplit[0]));
        dailyQuestionTp.setMinute(Integer.parseInt(dailyQuestionTimeSplit[1]));
    }

    @Override
    protected void onPause() {
        super.onPause();
        int hour = dailyQuestionTp.getHour();
        int minute = dailyQuestionTp.getMinute();
        sharPref.edit()
                .putString(getString(com.example.database.R.string.daily_question_time), hour + ":" + minute)
                .apply();

        WorkerFactory wf = new WorkerFactory(this);
        wf.enqueueNewNotificationRequest();
        Log.i(TAG, String.format("Daily question time changed to %d:%d", hour, minute));
    }
}
