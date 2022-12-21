package com.example.op.activity.user;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.database.AppDatabase;
import com.example.database.dao.ControlTextQuestionDao;
import com.example.database.entity.ControlTextQuestion;
import com.example.database.entity.ControlTextUserAnswer;
import com.example.op.R;
import com.example.op.activity.MainActivity;
import com.example.op.exception.ResourceNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuestionActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = QuestionActivity.class.getName();
    private AppDatabase database;
    private List<Integer> answers;
    private Button firstAnswerBtn, secondAnswerBtn, thirdAnswerBtn, fourthAnswerBtn;
    private TextView questionTv;
    private int questionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control_question);

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

        database = AppDatabase.getDatabaseInstance(this);

        ControlTextQuestionDao controlTextQuestionDao = database.controlTextQuestionDao();
        Integer questionsCount = controlTextQuestionDao.getAllCount();
        Random rand = new Random();
        questionId = rand.nextInt(questionsCount) + 1;

        ControlTextQuestion controlTextQuestion = controlTextQuestionDao.getById(questionId).orElseThrow(
                () -> new ResourceNotFoundException(TAG, "ControlTextQuestion with given id doesn't exist")
        );

        prepareAllAnswers(controlTextQuestion, rand);
        prepareUI(controlTextQuestion, allAnswerButtons);
    }

    private void prepareAllAnswers(ControlTextQuestion controlTextQuestion, Random rand) {
        Integer correctAnswer = controlTextQuestion.getCorrectAnswer();
        answers = new ArrayList<>();
        answers.add(controlTextQuestion.getCorrectAnswer());
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

    private void prepareUI(ControlTextQuestion controlTextQuestion, List<Button> allAnswerButtons) {
        String textQuestion = controlTextQuestion.getTextQuestion();
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
        var controlTextUserAnswer = new ControlTextUserAnswer(null, questionId, integerAnswer, LocalTime.now(), LocalDate.now());
        database.controlTextUserAnswerDao().insert(controlTextUserAnswer);
        Log.i(TAG, "User answered on control text question nr." + questionId +
                " with following answer: " + controlTextUserAnswer);
        Intent intent = new Intent(this, MainActivity.class);
        finish();
        startActivity(intent);
    }
}
