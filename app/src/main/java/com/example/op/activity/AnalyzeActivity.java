package com.example.op.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.op.R;

public class AnalyzeActivity extends AppCompatActivity implements View.OnClickListener {

    Button phoneActivityAnalyzeButton, fitbitActivityAnalyzeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analyze);

        phoneActivityAnalyzeButton = (Button) findViewById(R.id.phoneActivityAnalyzeButton);
        fitbitActivityAnalyzeButton = (Button) findViewById(R.id.fitbitActivityAnalyzeButton);

        phoneActivityAnalyzeButton.setOnClickListener(this);
        fitbitActivityAnalyzeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.phoneActivityAnalyzeButton) {

        } else if (v.getId() == R.id.fitbitActivityAnalyzeButton) {
            Intent intent = new Intent(this, FitbitAnalyzeActivity.class);
            startActivity(intent);
        }
    }
}
