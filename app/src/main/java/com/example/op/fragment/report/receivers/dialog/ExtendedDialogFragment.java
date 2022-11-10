package com.example.op.fragment.report.receivers.dialog;

import android.content.Context;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public abstract class ExtendedDialogFragment extends DialogFragment {

    protected abstract void updateListViewWithGivenData(Context context, ListView listView, ArrayList<String> data);

}
