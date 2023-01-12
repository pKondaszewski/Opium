package com.example.op.fragment.report;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.database.AppDatabase;
import com.example.database.entity.Profile;
import com.example.op.R;

import java.time.LocalDate;
import java.util.List;

public class GeneralReportFragment extends ReportFragment {
    private static final String TAG = GeneralReportFragment.class.getName();
    private Context context;
    private AppDatabase database;
    private SharedPreferences sharPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        context = getContext();
        database = AppDatabase.getDatabaseInstance(context);
        sharPref = context.getSharedPreferences(context.getString(com.example.database.R.string.opium_preferences), Context.MODE_PRIVATE);
        return inflater.inflate(R.layout.fragment_general_report, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        TextView dateValueTv = view.findViewById(R.id.text_view_date);
        TextView nameValueTv = view.findViewById(R.id.text_view_name_value);

        LocalDate now = LocalDate.now();

        TextView analyzeResultValueTv = view.findViewById(R.id.text_view_analyze_result_value);
        analyzeResultValueTv.setText(setupResults(now, sharPref, database));
        setupPersonalData(now, List.of(dateValueTv, nameValueTv), database);

        Button sendReportBtn = view.findViewById(R.id.button_send_report);
        sendReportBtn.setOnClickListener(v -> {
            boolean isMailSent = sendMail(TAG, getString(R.string.report_activity_title), getGeneralReportMessage(context), context);
            boolean isSmsSent = sendSms(TAG, getGeneralReportMessage(context), context);
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
        Profile profile = database.profileDao().get().get();
        personalDataViews.get(0).setText(presentDate.format(dateTimeFormatter));
        personalDataViews.get(1).setText(String.format("%s %s", profile.getFirstname(), profile.getLastname()));
        if (personalDataViews.size() > 2) {
            personalDataViews.get(2).setText(profile.getBirthdate().format(dateTimeFormatter));
        }
    }
}
