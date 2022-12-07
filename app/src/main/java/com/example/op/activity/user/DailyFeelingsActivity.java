package com.example.op.activity.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.database.AppDatabase;
import com.example.database.entity.DailyFeelings;
import com.example.op.R;
import com.example.op.activity.extra.TranslatedAppCompatActivity;
import com.example.op.utils.JsonManipulator;
import com.example.op.utils.simple.SimpleTextWatcher;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import co.ankurg.expressview.ExpressView;
import co.ankurg.expressview.OnCheckListener;
import lombok.SneakyThrows;

public class DailyFeelingsActivity extends TranslatedAppCompatActivity implements View.OnClickListener,
        OnCheckListener {

    private final ArrayList<String> ailments = new ArrayList<>();
    private AppDatabase database;
    private Button applyBtn;
    private EditText noteTextInputEt, otherAilmentsEt;
    private ExpressView tragicMoodBtn, badMoodBtn, okMoodBtn, goodMoodBtn, greatMoodBtn, feverAilmentsBtn,
            coldAilmentsBtn, coughAilmentsBtn, lethargyAilmentsBtn, soreThroatAilmentsBtn, headacheAilmentsBtn,
            vomitingAilmentsBtn, diarrheaAilmentsBtn, otherAilmentsBtn, uncheckedBtn;
    private LinearLayout otherAilmentsLl;
    private LocalDate dailyFeelingsDate;
    private String otherAilments, mood;

    @SneakyThrows
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_feelings);

        database = AppDatabase.getDatabaseInstance(this);
        dailyFeelingsDate = LocalDate.now();
        mood = "";

        otherAilmentsLl = findViewById(R.id.linear_layout_other_ailments);
        greatMoodBtn = findViewById(R.id.button_great_mood);
        goodMoodBtn = findViewById(R.id.button_good_mood);
        okMoodBtn = findViewById(R.id.button_ok_mood);
        badMoodBtn = findViewById(R.id.button_bad_mood);
        tragicMoodBtn = findViewById(R.id.button_tragic_mood);
        feverAilmentsBtn = findViewById(R.id.button_fever_ailments);
        coldAilmentsBtn = findViewById(R.id.button_cold_ailments);
        coughAilmentsBtn = findViewById(R.id.button_cough_ailments);
        lethargyAilmentsBtn = findViewById(R.id.button_lethargy_ailments);
        soreThroatAilmentsBtn = findViewById(R.id.button_sore_throat_ailments);
        headacheAilmentsBtn = findViewById(R.id.button_headache_ailments);
        vomitingAilmentsBtn = findViewById(R.id.button_vomiting_ailments);
        diarrheaAilmentsBtn = findViewById(R.id.button_diarrhea_ailments);
        otherAilmentsBtn = findViewById(R.id.button_other_ailments);
        noteTextInputEt = findViewById(R.id.edit_text_note_text_input);
        applyBtn = findViewById(R.id.button_apply);
        uncheckedBtn = tragicMoodBtn;

        greatMoodBtn.setOnCheckListener(this);
        goodMoodBtn.setOnCheckListener(this);
        okMoodBtn.setOnCheckListener(this);
        badMoodBtn.setOnCheckListener(this);
        tragicMoodBtn.setOnCheckListener(this);
        feverAilmentsBtn.setOnCheckListener(this);
        coldAilmentsBtn.setOnCheckListener(this);
        coughAilmentsBtn.setOnCheckListener(this);
        otherAilmentsBtn.setOnCheckListener(this);
        noteTextInputEt.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                applyButtonVisibilityCheck();
            }
        });
        applyBtn.setOnClickListener(this);

        Intent intent = getIntent();
        if (Objects.equals(intent.getAction(), Intent.ACTION_ASSIST)) {
            String jsonString = intent.getStringExtra(getString(R.string.daily_feelings_as_json));
            setupDailyFeelingsFromTreatmentHistory(jsonString);
        }
        String formattedDate;
        SharedPreferences sharPref = getSharedPreferences(getString(R.string.opium_preferences), MODE_PRIVATE);
        String lang = sharPref.getString("language", "en");
        if (lang.equals("pl")) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
            formattedDate = dailyFeelingsDate.format(formatter);
        } else {
            formattedDate = dailyFeelingsDate.toString();
        }
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(formattedDate);
    }

    private void setupDailyFeelingsFromTreatmentHistory(String jsonString) {
        DailyFeelings intentDailyFeelings = JsonManipulator.parseDailyFeelings(jsonString);
        dailyFeelingsDate = intentDailyFeelings.getDateOfDailyFeelings();
        String mood = intentDailyFeelings.getMood();
        List<String> ailments = intentDailyFeelings.getAilments();
        otherAilments = intentDailyFeelings.getOtherAilments();
        String note = intentDailyFeelings.getNote();

        activateMood(mood);
        activateAilments(ailments);
        setNote(note);
    }

    private void activateMood(String mood) {
        if (mood == null) {
            return;
        }
        switch (mood) {
            case "great":
                greatMoodBtn.performClick();
                break;
            case "good":
                goodMoodBtn.performClick();
                break;
            case "ok":
                okMoodBtn.performClick();
                break;
            case "bad":
                badMoodBtn.performClick();
                break;
            case "tragic":
                tragicMoodBtn.performClick();
                break;
        }
    }

    private void activateAilments(List<String> ailments) {
        if (ailments == null) {
            return;
        }
        for (String ailment : ailments) {
            switch (ailment) {
                case "fever":
                    feverAilmentsBtn.performClick();
                    break;
                case "cold":
                    coldAilmentsBtn.performClick();
                    break;
                case "cough":
                    coughAilmentsBtn.performClick();
                    break;
                case "lethargy":
                    lethargyAilmentsBtn.performClick();
                    break;
                case "sore throat":
                    soreThroatAilmentsBtn.performClick();
                    break;
                case "headache":
                    headacheAilmentsBtn.performClick();
                    break;
                case "vomiting":
                    vomitingAilmentsBtn.performClick();
                    break;
                case "diarrhea":
                    diarrheaAilmentsBtn.performClick();
                    break;
                case "other":
                    otherAilmentsBtn.performClick();
                    break;
            }
        }
    }

    private void setNote(String note) {
        noteTextInputEt.setText(note);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_apply) {
            if (otherAilmentsEt != null) {
                otherAilments = otherAilmentsEt.getText().toString();
            }
            DailyFeelings dailyFeelings = new DailyFeelings(null, mood, ailments, otherAilments,
                    noteTextInputEt.getText().toString(), dailyFeelingsDate);
            Optional<DailyFeelings> dailyFeelingsOptional = database.dailyFeelingsDao().getByDate(dailyFeelingsDate);
            if (dailyFeelingsOptional.isPresent()) {
                int dailyFeelingsId = dailyFeelingsOptional.get().getId();
                dailyFeelings.setId(dailyFeelingsId);
                database.dailyFeelingsDao().update(dailyFeelings);
            } else {
                database.dailyFeelingsDao().insert(dailyFeelings);
            }
            Toast.makeText(this, getString(R.string.saved_toast), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra(getString(R.string.picked_date_as_epoch_day), dailyFeelingsDate.toEpochDay());
            setResult(AppCompatActivity.RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onChecked(ExpressView expressView) {
        int id = expressView.getId();
        if (id == R.id.button_great_mood) {
            setFocus(uncheckedBtn, greatMoodBtn);
            mood = "great";
        } else if (id == R.id.button_good_mood) {
            setFocus(uncheckedBtn, goodMoodBtn);
            mood = "good";
        } else if (id == R.id.button_ok_mood) {
            setFocus(uncheckedBtn, okMoodBtn);
            mood = "ok";
        } else if (id == R.id.button_bad_mood) {
            setFocus(uncheckedBtn, badMoodBtn);
            mood = "bad";
        } else if (id == R.id.button_tragic_mood) {
            setFocus(uncheckedBtn, tragicMoodBtn);
            mood = "tragic";
        } else if (id == R.id.button_fever_ailments) {
            ailments.add("fever");
        } else if (id == R.id.button_cold_ailments) {
            ailments.add("cold");
        } else if (id == R.id.button_cough_ailments) {
            ailments.add("cough");
        } else if (id == R.id.button_lethargy_ailments) {
            ailments.add("lethargy");
        } else if (id == R.id.button_sore_throat_ailments) {
            ailments.add("sore throat");
        } else if (id == R.id.button_headache_ailments) {
            ailments.add("headache");
        } else if (id == R.id.button_vomiting_ailments) {
            ailments.add("vomiting");
        } else if (id == R.id.button_diarrhea_ailments) {
            ailments.add("diarrhea");
        } else if (id == R.id.button_other_ailments) {
            ailments.add("other");
            otherAilmentsEt = new EditText(this);
            otherAilmentsEt.setMinimumWidth(350);
            otherAilmentsEt.setHint(getString(R.string.ailments_other_label));
            if (otherAilments != null) {
                otherAilmentsEt.setText(otherAilments);
            }
            otherAilmentsLl.addView(otherAilmentsEt);
        }
        applyButtonVisibilityCheck();
    }

    private void setFocus(ExpressView uncheckedBtn, ExpressView checkedBtn) {
        if (!uncheckedBtn.equals(checkedBtn)) {
            checkedBtn.setChecked(true);
            uncheckedBtn.setChecked(false);
        }
        this.uncheckedBtn = checkedBtn;
    }

    @Override
    public void onUnChecked(ExpressView expressView) {
        int id = expressView.getId();
        if (id == R.id.button_great_mood || id == R.id.button_good_mood ||
            id == R.id.button_ok_mood || id == R.id.button_bad_mood || id == R.id.button_tragic_mood) {
            mood = "";
        } else if (id == R.id.button_fever_ailments) {
            ailments.remove("fever");
            System.out.println(mood);
        } else if (id == R.id.button_cold_ailments) {
            ailments.remove("cold");
        } else if (id == R.id.button_cough_ailments) {
            ailments.remove("cough");
        } else if (id == R.id.button_lethargy_ailments) {
            ailments.remove("lethargy");
        } else if (id == R.id.button_sore_throat_ailments) {
            ailments.remove("sore throat");
        } else if (id == R.id.button_headache_ailments) {
            ailments.remove("headache");
        } else if (id == R.id.button_vomiting_ailments) {
            ailments.remove("vomiting");
        } else if (id == R.id.button_diarrhea_ailments) {
            ailments.remove("diarrhea");
        } else if (id == R.id.button_other_ailments) {
            ailments.remove("other");
            otherAilmentsLl.removeAllViews();
        }
        applyButtonVisibilityCheck();
    }

    private void applyButtonVisibilityCheck() {
        if (mood.equals("") && ailments.isEmpty() && noteTextInputEt.getText().toString().equals("")) {
            applyBtn.setVisibility(View.INVISIBLE);
        } else {
            applyBtn.setVisibility(View.VISIBLE);
        }
    }
}
