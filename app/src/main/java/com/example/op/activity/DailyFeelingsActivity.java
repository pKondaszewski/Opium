package com.example.op.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.op.R;
import com.example.op.database.AppDatabase;
import com.example.op.database.entity.DailyFeelings;
import com.example.op.database.entity.FitbitSpO2Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import co.ankurg.expressview.ExpressView;
import co.ankurg.expressview.OnCheckListener;
import lombok.Setter;

public class DailyFeelingsActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener, OnCheckListener {

    AppDatabase database;
    DailyFeelings dailyFeelings;
    ExpressView tragicMoodButton, badMoodButton, okMoodButton, goodMoodButton, greatMoodButton;
    ExpressView feverAilmentsButton, coldAilmentsButton, coughAilmentsButton,
            lethargyAilmentsButton, soreThroatAilmentsButton, headacheAilmentsButton,
            vomitingAilmentsButton, diarrheaAilmentsButton;

    String mood;
    List<String> ailments;
    String note;

    SeekBar sleepQualitySeekBar;
    TextView sleepQualityValueTextView;
    EditText noteTextInputEditText;
    Button applyButton;
    ExpressView uncheckedButton;
    @Setter
    LocalDate currentDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_feelings);

        currentDate = LocalDate.now();
        mood = "-";
        ailments = List.of("-");
        note = "-";

        database = AppDatabase.getDatabaseInstance(this);

        dailyFeelings = new DailyFeelings();

        tragicMoodButton = (ExpressView) findViewById(R.id.tragicMoodButton);
        badMoodButton = (ExpressView) findViewById(R.id.badMoodButton);
        okMoodButton = (ExpressView) findViewById(R.id.okMoodButton);
        goodMoodButton = (ExpressView) findViewById(R.id.goodMoodButton);
        greatMoodButton = (ExpressView) findViewById(R.id.greatMoodButton);

        feverAilmentsButton = (ExpressView) findViewById(R.id.feverAilmentsButton);
        coldAilmentsButton = (ExpressView) findViewById(R.id.coldAilmentsButton);
        coughAilmentsButton = (ExpressView) findViewById(R.id.coughAilmentsButton);
        lethargyAilmentsButton = (ExpressView) findViewById(R.id.lethargyAilmentsButton);
        soreThroatAilmentsButton = (ExpressView) findViewById(R.id.soreThroatAilmentsButton);
        headacheAilmentsButton = (ExpressView) findViewById(R.id.headacheAilmentsButton);
        vomitingAilmentsButton = (ExpressView) findViewById(R.id.vomitingAilmentsButton);
        diarrheaAilmentsButton = (ExpressView) findViewById(R.id.diarrheaAilmentsButton);

        uncheckedButton = tragicMoodButton;

        sleepQualitySeekBar = (SeekBar) findViewById(R.id.sleepQualitySeekBar);
        sleepQualityValueTextView = (TextView) findViewById(R.id.sleepQualityValueTextView);

        noteTextInputEditText = (EditText) findViewById(R.id.noteTextInputEditText);

        applyButton = (Button) findViewById(R.id.applyButton);

        tragicMoodButton.setOnCheckListener(this);
        badMoodButton.setOnCheckListener(this);
        okMoodButton.setOnCheckListener(this);
        goodMoodButton.setOnCheckListener(this);
        greatMoodButton.setOnCheckListener(this);

        feverAilmentsButton.setOnCheckListener(this);
        coldAilmentsButton.setOnCheckListener(this);
        coughAilmentsButton.setOnCheckListener(this);

        sleepQualitySeekBar.setOnSeekBarChangeListener(this);

        applyButton.setOnClickListener(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(currentDate.toString());
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.applyButton) {
            dailyFeelings.setMood(mood);
            dailyFeelings.setAilments(ailments);
            dailyFeelings.setNote(validateNoteText(noteTextInputEditText.getText().toString()));
            dailyFeelings.setDateOfDailyFeelings(currentDate);
            database.dailyFeelingsDao().insert(dailyFeelings);
            Toast.makeText(this, "Zapisano", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private String validateNoteText(String string) {
        return string.equals("") ? "-" : string;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        sleepQualityValueTextView.setText(String.valueOf(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private void setFocus(ExpressView uncheckedButton, ExpressView checkedButton) {
        if (!uncheckedButton.equals(checkedButton)) {
            checkedButton.setChecked(true);
            uncheckedButton.setChecked(false);
        }
        this.uncheckedButton = checkedButton;
    }

    @Override
    public void onChecked(ExpressView expressView) {
        int id = expressView.getId();
        if (id == R.id.greatMoodButton) {
            setFocus(uncheckedButton, greatMoodButton);
            mood = "great";
        } else if (id == R.id.goodMoodButton) {
            setFocus(uncheckedButton, goodMoodButton);
            mood = "good";
        } else if (id == R.id.okMoodButton) {
            setFocus(uncheckedButton, okMoodButton);
            mood = "ok";
        } else if (id == R.id.badMoodButton) {
            setFocus(uncheckedButton, badMoodButton);
            mood = "bad";
        } else if (id == R.id.tragicMoodButton) {
            setFocus(uncheckedButton, tragicMoodButton);
            mood = "tragic";
        } else if (id == R.id.feverAilmentsButton) {
            ailments.add("fever");
        } else if (id == R.id.coldAilmentsButton) {
            ailments.add("cold");
        } else if (id == R.id.coughAilmentsButton) {
            ailments.add("cough");
        } else if (id == R.id.lethargyAilmentsButton) {
            ailments.add("lethargy");
        } else if (id == R.id.soreThroatAilmentsButton) {
            ailments.add("sore throat");
        } else if (id == R.id.headacheAilmentsButton) {
            ailments.add("headache");
        } else if (id == R.id.vomitingAilmentsButton) {
            ailments.add("vomiting");
        } else if (id == R.id.diarrheaAilmentsButton) {
            ailments.add("diarrhea");
        }
    }

    @Override
    public void onUnChecked(ExpressView expressView) {
        int id = expressView.getId();
        if (id == R.id.feverAilmentsButton) {
            ailments.remove("fever");
        } else if (id == R.id.coldAilmentsButton) {
            ailments.remove("cold");
        } else if (id == R.id.coughAilmentsButton) {
            ailments.remove("cough");
        } else if (id == R.id.lethargyAilmentsButton) {
            ailments.remove("lethargy");
        } else if (id == R.id.soreThroatAilmentsButton) {
            ailments.remove("sore throat");
        } else if (id == R.id.headacheAilmentsButton) {
            ailments.remove("headache");
        } else if (id == R.id.vomitingAilmentsButton) {
            ailments.remove("vomiting");
        } else if (id == R.id.diarrheaAilmentsButton) {
            ailments.remove("diarrhea");
        }
    }
}
