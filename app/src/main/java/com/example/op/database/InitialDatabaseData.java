package com.example.op.database;

import com.example.op.database.dao.ControlTextQuestionDao;
import com.example.op.database.dao.ControlTextUserAnswerDao;
import com.example.op.database.entity.ControlTextQuestion;
import com.example.op.database.entity.ControlTextUserAnswer;

import java.time.LocalDate;
import java.time.LocalTime;

public class InitialDatabaseData {

    public static void initControlTextQuestions(AppDatabase database) {
        ControlTextQuestionDao controlTextQuestionDao = database.controlTextQuestionDao();
        if (controlTextQuestionDao.getAll().size() == 0) {
            controlTextQuestionDao.insert(new ControlTextQuestion(null, "7+5=?", 12));
        }
        ControlTextUserAnswerDao controlTextUserAnswerDao = database.controlTextUserAnswerDao();
        controlTextUserAnswerDao.insert(new ControlTextUserAnswer(null, 1,1, LocalTime.now(), LocalDate.now()));
    }
}
