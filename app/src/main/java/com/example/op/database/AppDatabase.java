package com.example.op.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.op.database.dao.ControlTextQuestionDao;
import com.example.op.database.dao.ControlTextUserAnswerDao;
import com.example.op.database.dao.DailyFeelingsDao;
import com.example.op.database.dao.FitbitAccessTokenDao;
import com.example.op.database.dao.FitbitSpo2DataDao;
import com.example.op.database.dao.FitbitStepsDataDao;
import com.example.op.database.dao.PhoneLocalizationDao;
import com.example.op.database.entity.ControlTextQuestion;
import com.example.op.database.entity.ControlTextUserAnswer;
import com.example.op.database.entity.DailyFeelings;
import com.example.op.database.entity.FitbitAccessToken;
import com.example.op.database.entity.FitbitSpO2Data;
import com.example.op.database.entity.FitbitStepsData;
import com.example.op.database.entity.PhoneLocalization;

@Database(entities = {FitbitAccessToken.class, ControlTextQuestion.class, ControlTextUserAnswer.class,
                        DailyFeelings.class, FitbitStepsData.class, FitbitSpO2Data.class, PhoneLocalization.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    public abstract FitbitAccessTokenDao fitbitAccessTokenDao();

    public abstract ControlTextQuestionDao controlTextQuestionDao();

    public abstract ControlTextUserAnswerDao controlTextUserAnswerDao();

    public abstract DailyFeelingsDao dailyFeelingsDao();

    public abstract FitbitStepsDataDao fitbitStepsDataDao();

    public abstract FitbitSpo2DataDao fitbitSpO2DataDao();

    public abstract PhoneLocalizationDao phoneLocalizationDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabaseInstance(Context context) {
        if(INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "DB_NAME")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
