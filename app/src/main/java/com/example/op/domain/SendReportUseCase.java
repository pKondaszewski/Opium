package com.example.op.domain;

import android.content.Context;
import android.telephony.SmsManager;

import com.example.database.AppDatabase;
import com.example.database.entity.Profile;

public class SendReportUseCase {

    AppDatabase database;

    public SendReportUseCase(Context context) {
        database = AppDatabase.getDatabaseInstance(context);
    }

    public void sendReport() {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage("(650) 555-1212", null, "message", null, null);
    }

    public String getReport() {
        Profile profile = database.profileDao().get().orElse(new Profile());
        StringBuilder report = new StringBuilder();

        return report.toString();
    }
}
