package com.example.op.fragment.report.receivers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.database.AppDatabase;
import com.example.database.entity.EmailContact;
import com.example.op.R;
import com.example.op.exception.ResourceNotFoundException;
import com.example.op.fragment.report.receivers.dialog.AddEmailDialogFragment;
import com.example.op.utils.simple.SimpleTextWatcher;
import com.tomash.androidcontacts.contactgetter.entity.ContactData;
import com.tomash.androidcontacts.contactgetter.entity.Email;
import com.tomash.androidcontacts.contactgetter.main.contactsGetter.ContactsGetterBuilder;

import java.util.ArrayList;
import java.util.List;

public class EmailReportReceiversFragment extends ReportReceiversFragment {

    private static final String TAG = EmailReportReceiversFragment.class.getName();

    private AppDatabase database;
    private ArrayList<String> reportReceiversList, emailsList;
    private Context context;
    private EditText findEmailEt;
    private ListView allEmailsLv, reportReceiversLv;
    private TextView allEmailsTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        database = AppDatabase.getDatabaseInstance(getActivity());
        return inflater.inflate(R.layout.fragment_report_receivers, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        context = getContext();
        database = AppDatabase.getDatabaseInstance(context);

        Button addEmailBtn = view.findViewById(R.id.button_add_email);
        addEmailBtn.setOnClickListener(v -> {
            AddEmailDialogFragment addEmailDf =
                    new AddEmailDialogFragment(context, reportReceiversList, reportReceiversLv);
            addEmailDf.show(getActivity().getSupportFragmentManager(), TAG);
        });

        findEmailEt = view.findViewById(R.id.edit_text_find_contact);
        allEmailsLv = view.findViewById(R.id.list_view_all_contacts);
        allEmailsTv = view.findViewById(R.id.text_view_all_contacts);
        reportReceiversLv = view.findViewById(R.id.list_view_report_receivers);

        findEmailEt.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                getContacts(findEmailEt.getText().toString());
            }
        });
        reportReceiversLv.setOnItemClickListener((parent, clickedView, position, id) -> {
            String emailContact = reportReceiversList.get(position);
            String[] contactAsArray = emailContact.split(", ");
            EmailContact dbEmailContact = database.emailContactDao()
                    .findByNameAndEmailAddress(contactAsArray[0], contactAsArray[1]).orElseThrow(() ->
                            new ResourceNotFoundException(TAG,
                                    String.format("Can't find user with username: %s, email: %s",
                                            contactAsArray[0], contactAsArray[1])
                            )
                    );
            if (dbEmailContact.isFromContactBook()) {
                emailsList.add(reportReceiversList.get(position));
            }
            reportReceiversList.remove(position);
            database.emailContactDao().delete(dbEmailContact);
            Log.i(TAG, String.format("Removal of emailContact (name: %s, phoneNumber: %s) from database",
                    contactAsArray[0], contactAsArray[1]));
            updateListViewWithGivenData(reportReceiversLv, reportReceiversList);
        });
        allEmailsLv.setOnItemClickListener((parent, clickedView, position, id) -> {
            String emailContact = emailsList.get(position);
            String[] contactAsArray = emailContact.split(", ");
            EmailContact dbEmailContact = new EmailContact(null, contactAsArray[0], contactAsArray[1], true);
            database.emailContactDao().insert(dbEmailContact);
            Log.i(TAG, String.format("Insertion of emailContact (name: %s, emailAddress: %s) into database",
                    contactAsArray[0], contactAsArray[1]));
            reportReceiversList.add(emailsList.get(position));
            emailsList.remove(position);
            updateListViewWithGivenData(reportReceiversLv, reportReceiversList);
        });
        getReportReceivers();
        getContacts(null);
    }

    @Override
    protected void getReportReceivers() {
        contactPermissionComponentsVisibility(View.VISIBLE);
        reportReceiversList = new ArrayList<>();
        List<EmailContact> all = database.emailContactDao().getAll();
        for (EmailContact emailContact : all) {
            String name = emailContact.getName();
            String emailAddress = emailContact.getEmailAddress();
            reportReceiversList.add(String.format("%s, %s", name, emailAddress));
        }
        updateListViewWithGivenData(reportReceiversLv, reportReceiversList);
        Log.i(TAG, "Report receivers list has been created successfully");
    }

    @Override
    protected void getContacts(String template) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            contactPermissionComponentsVisibility(View.VISIBLE);
            emailsList = new ArrayList<>();
            ContactsGetterBuilder builder = new ContactsGetterBuilder(context).allFields();
            if (template != null) {
                builder = builder.withEmailLike(template);
            }
            List<ContactData> contactData = builder.buildList();
            for (ContactData contact : contactData) {
                String name = contact.getNameData().getFullName();
                List<Email> emailList = contact.getEmailList();
                if (!emailList.isEmpty()) {
                    String emailAddress = emailList.get(0).getMainData();
                    if (!database.emailContactDao().findByNameAndEmailAddress(name, emailAddress).isPresent()) {
                        emailsList.add(String.format("%s, %s", name, emailAddress));
                    }
                }
            }
            updateListViewWithGivenData(allEmailsLv, emailsList);
            Log.i(TAG, "Contacts list has been created successfully");
        } else {
            contactPermissionComponentsVisibility(View.INVISIBLE);
            Log.i(TAG, "Contacts list has not been created. Consider giving contact permission to application");
        }
    }

    @Override
    protected void contactPermissionComponentsVisibility(int visibilityState) {
        allEmailsTv.setVisibility(visibilityState);
        findEmailEt.setVisibility(visibilityState);
        allEmailsLv.setVisibility(visibilityState);
    }

    @Override
    protected void updateListViewWithGivenData(ListView listView, ArrayList<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
        listView.invalidate();
    }
}

