package com.example.op.activity.analyze;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.op.R;
import com.example.op.activity.extra.GlobalSetupAppCompatActivity;

public class AnalyzeActivity extends GlobalSetupAppCompatActivity implements View.OnClickListener {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        Button phoneActivityAnalyzeBtn = findViewById(R.id.button_analyze_phone_activity);
        Button fitbitActivityAnalyzeBtn = findViewById(R.id.button_analyze_fitbit_activity);
        Button expertSystemAnalyzeBtn = findViewById(R.id.button_analyze_expert_system);

        phoneActivityAnalyzeBtn.setOnClickListener(this);
        fitbitActivityAnalyzeBtn.setOnClickListener(this);
        expertSystemAnalyzeBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_analyze_phone_activity) {
            Intent intent = new Intent(this, PhoneAnalyzeActivity.class);
            startActivity(intent);
        } else if (id == R.id.button_analyze_fitbit_activity) {
            Intent intent = new Intent(this, FitbitAnalyzeActivity.class);
            startActivity(intent);
        } else if (id == R.id.button_analyze_expert_system) {
            Intent intent = new Intent(this, ExpertSystemActivity.class);
            startActivity(intent);
        }
    }
}
