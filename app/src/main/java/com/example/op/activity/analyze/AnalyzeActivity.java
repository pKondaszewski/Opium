package com.example.op.activity.analyze;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.op.R;
import com.example.op.activity.PhoneAnalyzeActivity;

public class AnalyzeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        Button phoneActivityAnalyzeBtn = findViewById(R.id.button_analyze_phone_activity);
        Button fitbitActivityAnalyzeBtn = findViewById(R.id.button_analyze_fitbit_activity);

        phoneActivityAnalyzeBtn.setOnClickListener(this);
        fitbitActivityAnalyzeBtn.setOnClickListener(this);
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
        }
    }
}
