package com.example.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.database.dao.DailyFeelingsDao;
import com.example.database.dao.DailyQuestionAnswerDao;
import com.example.database.dao.DailyQuestionDao;
import com.example.database.dao.EmailContactDao;
import com.example.database.dao.ExpertSystemResultDao;
import com.example.database.dao.FitbitAccessTokenDao;
import com.example.database.dao.FitbitSpo2DataDao;
import com.example.database.dao.FitbitStepsDataDao;
import com.example.database.dao.PhoneContactDao;
import com.example.database.dao.PhoneLocalizationDao;
import com.example.database.dao.PhoneMovementDao;
import com.example.database.dao.ProfileDao;
import com.example.database.entity.DailyFeelings;
import com.example.database.entity.DailyQuestion;
import com.example.database.entity.DailyQuestionAnswer;
import com.example.database.entity.EmailContact;
import com.example.database.entity.ExpertSystemResult;
import com.example.database.entity.FitbitAccessToken;
import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;
import com.example.database.entity.PhoneContact;
import com.example.database.entity.PhoneLocalization;
import com.example.database.entity.PhoneMovement;
import com.example.database.entity.Profile;

import java.util.concurrent.Executors;

@Database(entities = {FitbitAccessToken.class, DailyQuestion.class, DailyQuestionAnswer.class,
                        DailyFeelings.class, FitbitStepsData.class, FitbitSpO2Data.class, PhoneLocalization.class,
                        PhoneMovement.class, Profile.class, PhoneContact.class, EmailContact.class, ExpertSystemResult.class},
          version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract FitbitAccessTokenDao fitbitAccessTokenDao();
    public abstract DailyQuestionDao dailyQuestionDao();
    public abstract DailyQuestionAnswerDao dailyQuestionAnswerDao();
    public abstract DailyFeelingsDao dailyFeelingsDao();
    public abstract FitbitStepsDataDao fitbitStepsDataDao();
    public abstract FitbitSpo2DataDao fitbitSpO2DataDao();
    public abstract PhoneLocalizationDao phoneLocalizationDao();
    public abstract PhoneMovementDao phoneMovementDao();
    public abstract ProfileDao profileDao();
    public abstract PhoneContactDao phoneContactDao();
    public abstract EmailContactDao emailContactDao();
    public abstract ExpertSystemResultDao expertSystemResultDao();

    private static volatile AppDatabase INSTANCE;

    public synchronized static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = buildDatabase(context);
        }
        return INSTANCE;
    }

    private static AppDatabase buildDatabase(final Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "OPIUM_DB")
                .createFromAsset("database/opium.db")
                .allowMainThreadQueries()
                .build();
    }
}
