package com.example.op.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.op.R;
import com.example.op.database.AppDatabase;
import com.example.op.database.entity.ControlTextQuestion;
import com.example.op.database.entity.ControlTextUserAnswer;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "QuestionActivity";
    private AppDatabase database;
    private int questionId;
    private String notificationStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_question);

        notificationStart = getIntent().getStringExtra("EXTRA_SESSION_ID");
        Log.i(TAG, "x = " + notificationStart);

        TextView questionTextView = (TextView) findViewById(R.id.questionTextView);
        Button firstAnswerButton = (Button) findViewById(R.id.firstAnswerButton);
        Button secondAnswerButton = (Button) findViewById(R.id.secondAnswerButton);
        Button thirdAnswerButton = (Button) findViewById(R.id.thirdAnswerButton);
        Button fourthAnswerButton = (Button) findViewById(R.id.fourthAnswerButton);

        firstAnswerButton.setOnClickListener(this);
        secondAnswerButton.setOnClickListener(this);
        thirdAnswerButton.setOnClickListener(this);
        fourthAnswerButton.setOnClickListener(this);

        List<Button> allAnswerButtons = new ArrayList<>();
        allAnswerButtons.add(firstAnswerButton);
        allAnswerButtons.add(secondAnswerButton);
        allAnswerButtons.add(thirdAnswerButton);
        allAnswerButtons.add(fourthAnswerButton);

        database = AppDatabase.getDatabaseInstance(this);
        List<ControlTextQuestion> allQuestions = database.controlTextQuestionDao().getAll();

        Random rand = new Random();
        questionId = rand.nextInt(allQuestions.size()) + 1;

        ControlTextQuestion controlTextQuestion = database.controlTextQuestionDao().getById(questionId);

        String textQuestion = controlTextQuestion.getTextQuestion();

        questionTextView.setText(textQuestion);
        firstAnswerButton.setText("12");
        secondAnswerButton.setText("13");
        thirdAnswerButton.setText("14");
        fourthAnswerButton.setText("15");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.firstAnswerButton:
                database.controlTextUserAnswerDao().insert(new ControlTextUserAnswer(null, questionId, 1, LocalTime.now(), LocalDate.now()));
                break;
            case R.id.secondAnswerButton:
                database.controlTextUserAnswerDao().insert(new ControlTextUserAnswer(null, questionId, 2, LocalTime.now(), LocalDate.now()));
                break;
            case R.id.thirdAnswerButton:
                database.controlTextUserAnswerDao().insert(new ControlTextUserAnswer(null, questionId, 3, LocalTime.now(), LocalDate.now()));
                break;
            case R.id.fourthAnswerButton:
                database.controlTextUserAnswerDao().insert(new ControlTextUserAnswer(null, questionId, 4, LocalTime.now(), LocalDate.now()));
                break;
        }
    }
}
