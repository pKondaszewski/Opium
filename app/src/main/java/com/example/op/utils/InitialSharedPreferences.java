package com.example.op.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.op.R;

public class InitialSharedPreferences {

    public static void initStartValues(Context context) {
        SharedPreferences sharPref = context.getSharedPreferences(context.getString(R.string.opium_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sharPref.edit();

        String gmailAddress = sharPref.getString("gmail_address", null);
        if (gmailAddress == null) {
            edit.putString("gmail_address", context.getString(com.example.database.R.string.gmail_address));
        }
        String gmailPassword = sharPref.getString("gmail_password", null);
        if (gmailPassword == null) {
            edit.putString("gmail_password", context.getString(com.example.database.R.string.gmail_password));
        }
        edit.apply();
    }
}
