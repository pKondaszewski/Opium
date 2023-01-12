package com.example.op.fragment.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;

import com.example.op.R;

public class AppLanguageDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getContext();
        SharedPreferences sharPref = context.getSharedPreferences(getString(com.example.database.R.string.opium_preferences), Context.MODE_PRIVATE);
        String language = "language";
        String languageDialogValue = "language_dialog_value";
        int checkedItemValue = sharPref.getInt(languageDialogValue, 0);
        SharedPreferences.Editor editor = sharPref.edit();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.language_dialog_title)).setSingleChoiceItems(
                new CharSequence[]{"EN", "PL"}, checkedItemValue, (dialog, which) -> {
                    if (which == 0) {
                        editor.putString(language, "EN");
                        editor.putInt(languageDialogValue, 0);
                    } else if (which == 1) {
                        editor.putString(language, "PL");
                        editor.putInt(languageDialogValue, 1);
                    }
                    editor.apply();
                    this.dismiss();
                    getActivity().recreate();
                }
        );
        return builder.create();
    }
}

