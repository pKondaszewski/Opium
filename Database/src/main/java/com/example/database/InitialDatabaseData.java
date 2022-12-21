package com.example.database;

import com.example.database.dao.ControlTextQuestionDao;
import com.example.database.dao.FitbitSpo2DataDao;
import com.example.database.dao.FitbitStepsDataDao;
import com.example.database.dao.PhoneLocalizationDao;
import com.example.database.dao.PhoneMovementDao;
import com.example.database.dao.ProfileDao;
import com.example.database.entity.ControlTextQuestion;
import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;
import com.example.database.entity.PhoneLocalization;
import com.example.database.entity.PhoneMovement;
import com.example.database.entity.Profile;

import java.time.LocalDate;
import java.time.LocalTime;

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
                    new Profile(null, "Jan", "Kowalski", LocalDate.of(2000, 1,1),"Male","123456789","test@gmail.com", new HomeAddress("Dworcowa 23B Mścice", "76-031", "Polska")));
        }
    }

    public static void initData(AppDatabase database) {
        PhoneLocalizationDao phoneLocalizationDao = database.phoneLocalizationDao();
        if (phoneLocalizationDao.getAll().isEmpty()) {
            phoneLocalizationDao.insert(new PhoneLocalization(1, LocalTime.now(), LocalDate.now(), 54.216635, 16.076754, new HomeAddress("Dworcowa 23B/2", "76-031", "Poland")));
            phoneLocalizationDao.insert(new PhoneLocalization(2, LocalTime.now(), LocalDate.now(), 54.216635, 16.076754, new HomeAddress("Dworcowa 23B/2", "76-031", "Poland")));
            phoneLocalizationDao.insert(new PhoneLocalization(3, LocalTime.now(), LocalDate.now(), 54.216635, 16.076754, new HomeAddress("Dworcowa 23B/2", "76-031", "Poland")));
        }
        PhoneMovementDao phoneMovementDao = database.phoneMovementDao();
        if (phoneMovementDao.getAll().isEmpty()) {
            phoneMovementDao.insert(new PhoneMovement(1, LocalDate.now(), LocalTime.of(1,2)));
            phoneMovementDao.insert(new PhoneMovement(2, LocalDate.now(), LocalTime.of(1,3)));
            phoneMovementDao.insert(new PhoneMovement(3, LocalDate.now(), LocalTime.of(1,4)));
            phoneMovementDao.insert(new PhoneMovement(4, LocalDate.now(), LocalTime.of(1,5)));
            phoneMovementDao.insert(new PhoneMovement(5, LocalDate.now(), LocalTime.of(1,6)));
            phoneMovementDao.insert(new PhoneMovement(6, LocalDate.now(), LocalTime.of(1,7)));
            phoneMovementDao.insert(new PhoneMovement(7, LocalDate.now(), LocalTime.of(5,8)));
        }
        FitbitStepsDataDao fitbitStepsDataDao = database.fitbitStepsDataDao();
        if (fitbitStepsDataDao.getAll().isEmpty()) {
            fitbitStepsDataDao.insert(new FitbitStepsData(7, LocalDate.now(), LocalTime.now(), "100"));
            fitbitStepsDataDao.insert(new FitbitStepsData(1, LocalDate.of(2022, 11,27), LocalTime.now(), "100"));
            fitbitStepsDataDao.insert(new FitbitStepsData(2, LocalDate.of(2022, 11,24), LocalTime.now(), "120"));
            fitbitStepsDataDao.insert(new FitbitStepsData(3, LocalDate.of(2022, 10,23), LocalTime.now(), "150"));
            fitbitStepsDataDao.insert(new FitbitStepsData(4, LocalDate.of(2022, 10,6), LocalTime.now(), "200"));
            fitbitStepsDataDao.insert(new FitbitStepsData(5, LocalDate.of(2022, 9,6), LocalTime.now(), "170"));
            fitbitStepsDataDao.insert(new FitbitStepsData(6, LocalDate.of(2022, 9,7), LocalTime.now(), "150"));
        }
        FitbitSpo2DataDao fitbitSpo2DataDao = database.fitbitSpO2DataDao();
        if (fitbitSpo2DataDao.getAll().isEmpty()) {
            fitbitSpo2DataDao.insert(new FitbitSpO2Data(7, LocalDate.now(), LocalTime.now(), "97"));
            fitbitSpo2DataDao.insert(new FitbitSpO2Data(1, LocalDate.of(2022, 11,27), LocalTime.now(), "99"));
            fitbitSpo2DataDao.insert(new FitbitSpO2Data(2, LocalDate.of(2022, 11,24), LocalTime.now(), "93"));
            fitbitSpo2DataDao.insert(new FitbitSpO2Data(3, LocalDate.of(2022, 10,23), LocalTime.now(), "87"));
            fitbitSpo2DataDao.insert(new FitbitSpO2Data(4, LocalDate.of(2022, 10,6), LocalTime.now(), "75"));
            fitbitSpo2DataDao.insert(new FitbitSpO2Data(5, LocalDate.of(2022, 9,6), LocalTime.now(), "89"));
            fitbitSpo2DataDao.insert(new FitbitSpO2Data(6, LocalDate.of(2022, 9,7), LocalTime.now(), "92"));
        }
    }
}
