package com.example.op.fragment.report.receivers;

import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.database.AppDatabase;

import java.time.LocalDate;
import java.util.List;

public abstract class ReportFragment extends Fragment {

    protected abstract void setupPersonalData(LocalDate presentDate, List<TextView> personalDataViews, AppDatabase database);

    protected abstract boolean sendMail();

    protected abstract boolean sendSms();
}
