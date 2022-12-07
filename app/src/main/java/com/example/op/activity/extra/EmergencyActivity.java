package com.example.op.activity.extra;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.op.R;

public class EmergencyActivity extends AppCompatActivity {

    private static final String TAG = EmergencyActivity.class.getName();
    private float screenX1, screenX2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        Button emergencyBtn = findViewById(R.id.button_emergency);
        emergencyBtn.setOnClickListener(v ->
            Log.i(TAG, "Emergency button pressed. Informing report receivers about it")
        );
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            screenX1 = event.getX();
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            screenX2 = event.getX();
            if (screenX2 - screenX1 > 0) {
                finish();
            }
        }
        return false;
    }
}
