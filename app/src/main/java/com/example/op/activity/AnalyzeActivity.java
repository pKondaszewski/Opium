package com.example.op.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.op.R;

public class AnalyzeActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        Button phoneActivityAnalyzeButton = findViewById(R.id.phoneActivityAnalyzeButton);
        Button fitbitActivityAnalyzeButton = findViewById(R.id.fitbitActivityAnalyzeButton);

        phoneActivityAnalyzeButton.setOnClickListener(this);
        fitbitActivityAnalyzeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.phoneActivityAnalyzeButton) {
            Intent intent = new Intent(this, PhoneAnalyzeActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.fitbitActivityAnalyzeButton) {
            Intent intent = new Intent(this, FitbitAnalyzeActivity.class);
            startActivity(intent);
        }
    }
}
