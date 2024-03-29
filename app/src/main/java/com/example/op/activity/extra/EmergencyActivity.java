package com.example.op.activity.extra;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.database.AppDatabase;
import com.example.database.entity.EmailContact;
import com.example.database.entity.PhoneContact;
import com.example.op.R;
import com.example.op.email.SendMailTask;

import java.util.List;

public class EmergencyActivity extends GlobalSetupAppCompatActivity {
    private static final String TAG = EmergencyActivity.class.getName();
    private float screenX1, screenX2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);

        Button emergencyBtn = findViewById(R.id.button_emergency);
        emergencyBtn.setOnClickListener(v -> {
            sendMail();
            sendSms();
            Toast.makeText(this, getString(R.string.emergency_toast_message), Toast.LENGTH_LONG).show();
            Log.i(TAG, "Emergency button pressed. Informing report receivers about it");
            finish();
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            screenX1 = event.getX();
        } else if (action == MotionEvent.ACTION_UP) {
            screenX2 = event.getX();
            if (screenX2 - screenX1 > 0) {
                finish();
            }
        }
        return false;
    }

    private void sendMail() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED) {
            AppDatabase database = AppDatabase.getInstance(this);
            List<EmailContact> emailContacts = database.emailContactDao().getAll();
            emailContacts.forEach(emailContact -> handleEmailSending(emailContact.getEmailAddress()));
        }
    }

    private void handleEmailSending(String emailAddress) {
        SendMailTask sendMailTask = new SendMailTask(this, emailAddress,"SOS!", getString(R.string.emergency_message), true);
        sendMailTask.execute();
    }

    private void sendSms() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            AppDatabase database = AppDatabase.getInstance(this);
            List<PhoneContact> phoneContacts = database.phoneContactDao().getAll();
            phoneContacts.forEach(phoneContact ->
                    smsManager.sendTextMessage(phoneContact.getPhoneNumber().replaceAll("\\s+",""),
                            null, getString(R.string.emergency_message), null, null));
        }
    }
}
