package com.example.op.utils;

import android.content.Context;

import com.example.op.R;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Translation {
    private final Context context;

    public String translateGender(String gender) {
        switch (gender) {
            case "Male":
                return context.getString(R.string.male_gender_spinner_item);
            case "Female":
                return context.getString(R.string.female_gender_spinner_item);
            case "Other":
                return context.getString(R.string.other_gender_spinner_item);
            default:
                break;
        }
        return null;
    }

    public String translateMood(String mood) {
        if (mood != null) {
            switch (mood) {
                case "great":
                    return context.getString(R.string.mood_greatFeelings_radio_button);
                case "good":
                    return context.getString(R.string.mood_goodFeelings_radio_button);
                case "ok":
                    return context.getString(R.string.mood_okFeelings_radio_button);
                case "bad":
                    return context.getString(R.string.mood_badFeelings_radio_button);
                case "tragic":
                    return context.getString(R.string.mood_tragicFeelings_radio_button);
                default:
                    break;
            }
        }
        return null;
    }

    public List<String> translateAilments(List<String> ailments) {
        return ailments.isEmpty() ?
                Collections.emptyList() : ailments.stream().map(this::translateAilment).collect(Collectors.toList());
    }

    private String translateAilment(String ailment) {
        if (ailment != null) {
            if (ailment.startsWith("fever")) {
                return context.getString(R.string.ailments_fever_button);
            } else if (ailment.startsWith("flu")) {
                return context.getString(R.string.ailments_flu_button);
            } else if (ailment.startsWith("cough")) {
                return context.getString(R.string.ailments_cough_button);
            } else if (ailment.startsWith("lethargy")) {
                return context.getString(R.string.ailments_lethargy_button);
            } else if (ailment.startsWith("sore throat")) {
                return context.getString(R.string.ailments_sore_throat_button);
            } else if (ailment.startsWith("headache")) {
                return context.getString(R.string.ailments_headache_button);
            } else if (ailment.startsWith("vomiting")) {
                return context.getString(R.string.ailments_vomiting_button);
            } else if (ailment.startsWith("diarrhea")) {
                return context.getString(R.string.ailments_diarrhea_button);
            } else if (ailment.startsWith("other")) {
                return context.getString(R.string.ailments_other_button) + ailment.substring(5);
            } else if (ailment.equals("")) {
                return "";
            }
        }
        return null;
    }
}
