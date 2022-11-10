package com.example.database;

import com.example.database.dao.ControlTextQuestionDao;
import com.example.database.dao.ProfileDao;
import com.example.database.entity.ControlTextQuestion;
import com.example.database.entity.Profile;

import java.time.LocalDate;

public class InitialDatabaseData {

    public static void initControlTextQuestions(AppDatabase database) {
        ControlTextQuestionDao controlTextQuestionDao = database.controlTextQuestionDao();
        if (controlTextQuestionDao.getAll().size() == 0) {
            controlTextQuestionDao.insert(new ControlTextQuestion(null, "7+5=?", 12));
            controlTextQuestionDao.insert(new ControlTextQuestion(null, "2+5=?", 7));
            controlTextQuestionDao.insert(new ControlTextQuestion(null, "5*3=?", 15));
        }
    }

    public static void initProfile(AppDatabase database) {
        ProfileDao profileDao = database.profileDao();
        if (!profileDao.get().isPresent()) {
            profileDao.insert(
                    new Profile(null, "", "", LocalDate.ofEpochDay(0L),"","","", new HomeAddress("", "", "")));
        }
    }
}
