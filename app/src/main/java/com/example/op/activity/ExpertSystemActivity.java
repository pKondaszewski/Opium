package com.example.op.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.expertsystem.ExpertSystem;
import com.example.expertsystem.ExpertSystemLevel;
import com.example.op.R;
import com.example.op.activity.extra.TranslatedAppCompatActivity;

public class ExpertSystemActivity extends TranslatedAppCompatActivity implements Runnable {

    private static final String TAG = ExpertSystemActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze_expert_system);

        Button launchExpertSystemBtn = findViewById(R.id.button_launch_expert_system);
        launchExpertSystemBtn.setOnClickListener(v -> {
            Thread thread = new Thread(this);
            thread.start();
        });
    }

    @Override
    public void run() {
        ExpertSystem expertSystem = new ExpertSystem(this);
        expertSystem.launch(ExpertSystemLevel.LOW);
        Log.i(TAG, "Expert system processing has finished successfully");
    }
}
