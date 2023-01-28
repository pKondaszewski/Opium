package com.example.op.activity.user;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.database.AppDatabase;
import com.example.database.dao.DailyQuestionDao;
import com.example.database.entity.DailyQuestion;
import com.example.database.entity.DailyQuestionAnswer;
import com.example.op.R;
import com.example.op.activity.MainActivity;
import com.example.op.activity.extra.GlobalSetupAppCompatActivity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DailyQuestionActivity extends GlobalSetupAppCompatActivity implements View.OnClickListener {
    private static final String TAG = DailyQuestionActivity.class.getName();
    private AppDatabase database;
    private List<Integer> answers;
    private Button firstAnswerBtn, secondAnswerBtn, thirdAnswerBtn, fourthAnswerBtn;
    private SharedPreferences sharPref;
    private TextView questionTv;
    private int questionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_question);

        questionTv = findViewById(R.id.text_view_question);
        firstAnswerBtn = findViewById(R.id.button_first_answer);
        secondAnswerBtn = findViewById(R.id.button_second_answer);
        thirdAnswerBtn = findViewById(R.id.button_third_answer);
        fourthAnswerBtn = findViewById(R.id.button_fourth_answer);

        firstAnswerBtn.setOnClickListener(this);
        secondAnswerBtn.setOnClickListener(this);
        thirdAnswerBtn.setOnClickListener(this);
        fourthAnswerBtn.setOnClickListener(this);
        List<Button> allAnswerButtons = List.of(firstAnswerBtn, secondAnswerBtn, thirdAnswerBtn, fourthAnswerBtn);

        database = AppDatabase.getInstance(this);
        sharPref = getSharedPreferences(getString(com.example.database.R.string.opium_preferences), MODE_PRIVATE);

        DailyQuestionDao dailyQuestionDao = database.dailyQuestionDao();
        Integer questionsCount = dailyQuestionDao.getAllCount();
        Random rand = new Random();
        questionId = rand.nextInt(questionsCount) + 1;

        DailyQuestion dailyQuestion = dailyQuestionDao.getById(questionId).get();
        prepareAllAnswers(dailyQuestion, rand);
        prepareUI(dailyQuestion, allAnswerButtons);
    }

    private void prepareAllAnswers(DailyQuestion dailyQuestion, Random rand) {
        Integer correctAnswer = dailyQuestion.getCorrectAnswer();
        answers = new ArrayList<>();
        answers.add(dailyQuestion.getCorrectAnswer());
        while(true) {
            int incorrectAnswer = rand.nextInt(correctAnswer - (correctAnswer / 2)) + correctAnswer;
            if (answers.contains(incorrectAnswer)) {
                continue;
            }
            answers.add(incorrectAnswer);
            if (answers.size() == 4) {
                break;
            }
        }
        Collections.shuffle(answers);
    }

    private void prepareUI(DailyQuestion dailyQuestion, List<Button> allAnswerButtons) {
        String textQuestion = dailyQuestion.getTextQuestion();
        questionTv.setText(textQuestion);
        for (Button button : allAnswerButtons) {
            int i = allAnswerButtons.indexOf(button);
            button.setText(String.valueOf(answers.get(i)));
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_first_answer) {
            insertAnswer(firstAnswerBtn);
        } else if (id == R.id.button_second_answer) {
            insertAnswer(secondAnswerBtn);
        } else if (id == R.id.button_third_answer) {
            insertAnswer(thirdAnswerBtn);
        } else if (id == R.id.button_fourth_answer) {
            insertAnswer(fourthAnswerBtn);
        }
    }

    private void insertAnswer(Button button) {
        int integerAnswer = Integer.parseInt(button.getText().toString());
        var dailyQuestionAnswer = new DailyQuestionAnswer(null, questionId, integerAnswer, LocalTime.now(), LocalDate.now());
        database.dailyQuestionAnswerDao().insert(dailyQuestionAnswer);
        Log.i(TAG, "User answered on daily question nr." + questionId +
                " with following answer: " + dailyQuestionAnswer);
        sharPref.edit().putString(getString(com.example.database.R.string.is_repeatable), "false").apply();
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}
