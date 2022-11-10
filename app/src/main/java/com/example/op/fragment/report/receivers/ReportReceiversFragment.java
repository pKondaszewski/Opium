package com.example.op.fragment.report.receivers;

import android.widget.ListView;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public abstract class ReportReceiversFragment extends Fragment {

    protected abstract void getReportReceivers();

    protected abstract void getContacts(String template);

    protected abstract void contactPermissionComponentsVisibility(int visibilityState);

    protected abstract void updateListViewWithGivenData(ListView listView, ArrayList<String> data);

}
