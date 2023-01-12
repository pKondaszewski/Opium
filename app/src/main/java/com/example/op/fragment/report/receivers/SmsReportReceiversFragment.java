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
import com.example.database.entity.PhoneContact;
import com.example.expertsystem.ResourceNotFoundException;
import com.example.op.R;
import com.example.op.fragment.report.receivers.dialog.AddContactDialogFragment;
import com.example.op.utils.simple.SimpleTextWatcher;
import com.tomash.androidcontacts.contactgetter.entity.ContactData;
import com.tomash.androidcontacts.contactgetter.main.contactsGetter.ContactsGetterBuilder;

import java.util.ArrayList;
import java.util.List;

public class SmsReportReceiversFragment extends ReportReceiversFragment {

    private static final String TAG = SmsReportReceiversFragment.class.getName();
    private AppDatabase database;
    private ArrayList<String> reportReceiversList, contactsList;
    private Context context;
    private EditText findContactEt;
    private ListView allContactsLv, reportReceiversLv;
    private TextView allContactsTv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        database = AppDatabase.getDatabaseInstance(getActivity());
        return inflater.inflate(R.layout.fragment_report_receivers, parent, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        context = getContext();
        database = AppDatabase.getDatabaseInstance(context);

        Button addContactBtn = view.findViewById(R.id.button_add_email);
        addContactBtn.setOnClickListener(v -> {
            AddContactDialogFragment addContactDf =
                    new AddContactDialogFragment(context, reportReceiversList, reportReceiversLv);
            addContactDf.show(getActivity().getSupportFragmentManager(), TAG);
        });

        findContactEt = view.findViewById(R.id.edit_text_find_contact);
        allContactsLv = view.findViewById(R.id.list_view_all_contacts);
        allContactsTv = view.findViewById(R.id.text_view_all_contacts);
        reportReceiversLv = view.findViewById(R.id.list_view_report_receivers);

        findContactEt.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                getContacts(findContactEt.getText().toString());
            }
        });
        reportReceiversLv.setOnItemClickListener((parent, clickedView, position, id) -> {
            String phoneContact = reportReceiversList.get(position);
            String[] contactAsArray = phoneContact.split(", ");
            PhoneContact dbPhoneContact = database.phoneContactDao()
                    .findByNameAndPhoneNumber(contactAsArray[0], contactAsArray[1]).get();
            if (dbPhoneContact.isFromContactBook()) {
                contactsList.add(reportReceiversList.get(position));
            }
            reportReceiversList.remove(position);
            database.phoneContactDao().delete(dbPhoneContact);
            Log.i(TAG, String.format("Removal of phoneContact (name: %s, phoneNumber: %s) from database",
                    contactAsArray[0], contactAsArray[1]));
            updateListViewWithGivenData(reportReceiversLv, reportReceiversList);
        });
        allContactsLv.setOnItemClickListener((parent, clickedView, position, id) -> {
            String phoneContact = contactsList.get(position);
            String[] contactAsArray = phoneContact.split(", ");
            PhoneContact dbPhoneContact = new PhoneContact(null, contactAsArray[0], contactAsArray[1], true);
            database.phoneContactDao().insert(dbPhoneContact);
            Log.i(TAG, String.format("Insertion of phoneContact (name: %s, phoneNumber: %s) into database",
                    contactAsArray[0], contactAsArray[1]));
            reportReceiversList.add(contactsList.get(position));
            contactsList.remove(position);
            updateListViewWithGivenData(reportReceiversLv, reportReceiversList);
        });
        getReportReceivers();
        getContacts(null);
    }

    @Override
    protected void getReportReceivers() {
        contactPermissionComponentsVisibility(View.VISIBLE);
        reportReceiversList = new ArrayList<>();
        List<PhoneContact> all = database.phoneContactDao().getAll();
        for (PhoneContact phoneContact : all) {
            String name = phoneContact.getName();
            String phoneNumber = phoneContact.getPhoneNumber();
            reportReceiversList.add(String.format("%s, %s", name, phoneNumber));
        }
        updateListViewWithGivenData(reportReceiversLv, reportReceiversList);
        Log.i(TAG, "Report receivers list has been created successfully");
    }

    @Override
    protected void getContacts(String template) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            contactPermissionComponentsVisibility(View.VISIBLE);
            contactsList = new ArrayList<>();
            ContactsGetterBuilder builder = new ContactsGetterBuilder(context).allFields().onlyWithPhones();
            if (template != null) {
                builder = builder.withNameLike(template);
            }
            List<ContactData> contactData = builder.buildList();
            for (ContactData contact : contactData) {
                String name = contact.getNameData().getFullName();
                String phoneNumber = contact.getPhoneList().get(0).getMainData();
                if (!database.phoneContactDao().findByNameAndPhoneNumber(name, phoneNumber).isPresent()) {
                    contactsList.add(String.format("%s, %s", name, phoneNumber));
                }
            }
            updateListViewWithGivenData(allContactsLv, contactsList);
            Log.i(TAG, "Contacts list has been created successfully");
        } else {
            contactPermissionComponentsVisibility(View.INVISIBLE);
            Log.i(TAG, "Contacts list has not been created. Consider giving contact permission to application");
        }
    }

    @Override
    protected void contactPermissionComponentsVisibility(int visibilityState) {
        allContactsTv.setVisibility(visibilityState);
        findContactEt.setVisibility(visibilityState);
    }

    @Override
    protected void updateListViewWithGivenData(ListView listView, ArrayList<String> data) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, data);
        listView.setAdapter(adapter);
        listView.invalidate();
    }
}
