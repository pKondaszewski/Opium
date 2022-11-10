package com.example.op.fragment.report;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.database.AppDatabase;
import com.example.database.entity.PhoneContact;
import com.example.database.entity.Profile;
import com.example.op.R;
import com.example.op.email.SendMail;
import com.example.op.exception.ResourceNotFoundException;
import com.example.op.fragment.report.receivers.ReportFragment;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeneralReportFragment extends ReportFragment {

    private static final String TAG = GeneralReportFragment.class.getName();
    private AppDatabase database;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        database = AppDatabase.getDatabaseInstance(getContext());
        return inflater.inflate(R.layout.fragment_general_report, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        context = getContext();

        TextView dateValueTv = view.findViewById(R.id.text_view_date_value);
        TextView nameValueTv = view.findViewById(R.id.text_view_name_value);
        setupPersonalData(LocalDate.now(), List.of(dateValueTv, nameValueTv), database);

        Button sendReportBtn = view.findViewById(R.id.button_send_report);
        sendReportBtn.setOnClickListener(v -> {
            boolean isMailSent = sendMail();
            boolean isSmsSent = sendSms();
            if (isMailSent || isSmsSent) {
                requireActivity().finish();
            } else {
                Toast.makeText(
                        context, "Can't send report. Check app permissions or internet connection",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void setupPersonalData(LocalDate presentDate, List<TextView> personalDataViews, AppDatabase database) {
        Profile profile = database.profileDao().get().orElseThrow(
                () -> new ResourceNotFoundException(TAG, "Profile doesn't exist in database")
        );
        personalDataViews.get(0).setText(presentDate.toString());
        personalDataViews.get(1).setText(String.format("%s %s", profile.getFirstname(), profile.getLastname()));
    }

    @Override
    protected boolean sendMail() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.INTERNET)
                == PackageManager.PERMISSION_GRANTED) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            database.emailContactDao().getAll().forEach(
                    emailContact -> {
                        SendMail sendMail = new SendMail(context, emailContact.getEmailAddress(), "Raport", "message");
                        executor.execute(sendMail);
                    }
            );
            Toast.makeText(context, "Email report sent", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "Email report sent successfully");
            return true;
        } else {
            Log.i(TAG, "Email report sent failure");
            return false;
        }
    }

    @Override
    protected boolean sendSms() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            SmsManager smsManager = SmsManager.getDefault();
            database.phoneContactDao().getAll().forEach(phoneContact ->
                    smsManager.sendTextMessage(phoneContact.getPhoneNumber(), null, "test", null, null));
            Toast.makeText(context, "SMS report sent", Toast.LENGTH_SHORT).show();
            Log.i(TAG, "SMS report sent successfully");
            return true;
        } else {
            Log.i(TAG, "SMS report sent failure");
            return false;
        }
    }
}
