package com.example.op.fragment.report.receivers.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;

import com.example.database.AppDatabase;
import com.example.database.entity.EmailContact;
import com.example.op.R;

import java.util.ArrayList;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class AddEmailDialogFragment extends ExtendedDialogFragment {

    private static final String TAG = AddEmailDialogFragment.class.getName();

    private final Context context;
    private final ArrayList<String> reportReceiversList;
    private final ListView reportReceiversLv;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        AppDatabase database = AppDatabase.getInstance(context);

        View view = inflater.inflate(R.layout.fragment_add_email, null);
        EditText nameEt = (EditText) view.findViewById(R.id.edit_text_add_contact_name);
        EditText emailEt = (EditText) view.findViewById(R.id.edit_text_add_contact_email);

        builder.setView(view)
                .setPositiveButton(getString(R.string.dialog_positive_button), (dialog, which) -> {
                    String name = nameEt.getText().toString();
                    String email = emailEt.getText().toString();
                    if (!email.isEmpty()) {
                        if (name.isEmpty()) {
                            name = getString(R.string.none_data);
                        }
                        EmailContact emailContact = new EmailContact(null, name, email, false);
                        database.emailContactDao().insert(emailContact);
                        Log.i(TAG, String.format("Insertion of emailContact (name: %s, emailAddress: %s) into database",
                                name, email));
                        String[] contactAsArray = new String[]{emailContact.getName(), emailContact.getEmailAddress()};
                        reportReceiversList.add(contactAsArray[0] + ", " + contactAsArray[1]);
                        updateListViewWithGivenData(context, reportReceiversLv, reportReceiversList);
                    }
                })
                .setNegativeButton(getString(R.string.dialog_negative_button), (dialog, id) ->
                        AddEmailDialogFragment.this.getDialog().cancel());
        return builder.create();
    }

    @Override
    protected void updateListViewWithGivenData(Context context, ListView listView, ArrayList<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
        listView.invalidate();
    }
}
