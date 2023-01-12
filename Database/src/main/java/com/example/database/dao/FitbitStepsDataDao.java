package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.database.entity.FitbitStepsData;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Dao
public interface FitbitStepsDataDao extends CrudDao<FitbitStepsData> {
    @Query("SELECT * FROM FitbitStepsData")
    List<FitbitStepsData> getAll();

    @Query("SELECT * FROM FitbitStepsData GROUP BY date HAVING MAX(time) ORDER BY date ASC LIMIT :dayLimit")
    List<FitbitStepsData> getHighestFitbitStepsDataValueByEveryDate(int dayLimit);

    @Query("SELECT * FROM FitbitStepsData WHERE strftime('%Y', date, '-23 days', '+2 months', '+6682 years') = :yearValue")
    List<FitbitStepsData> getAllFromYear(String yearValue);

    //AVG(stepsValue)
    @Query("SELECT AVG(stepsValue) FROM FitbitStepsData WHERE strftime('%m', date, '-23 days', '+2 months', '+6682 years') = :monthValue AND strftime('%Y', date, '-23 days', '+2 months', '+6682 years') = :yearValue GROUP BY date HAVING MAX(time)")
    Double getAverageFromMonth(String monthValue, String yearValue);

    @Query("SELECT * FROM FitbitStepsData WHERE date = :date ORDER BY id DESC LIMIT 1")
    Optional<FitbitStepsData> getNewestFitbitStepsDataByDate(LocalDate date);

    @Query("SELECT date FROM FitbitStepsData WHERE strftime('%m', date, '-23 days', '+2 months', '+6682 years') = :monthValue")
    List<LocalDate> getAllFromMonth(String monthValue);
}
