package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.database.entity.FitbitSpO2Data;
import com.example.database.entity.FitbitStepsData;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Dao
public interface FitbitSpo2DataDao extends CrudDao<FitbitSpO2Data> {
    @Query("SELECT * FROM FitbitSpO2Data")
    List<FitbitSpO2Data> getAll();

    @Query("SELECT * FROM FitbitSpO2Data WHERE spO2Value NOT NULL GROUP BY date HAVING MAX(time)")
    List<FitbitSpO2Data> getHighestFitbitSpO2DataValueByEveryDate();

    @Query("SELECT * FROM FitbitSpO2Data WHERE strftime('%Y', date, '-23 days', '+2 months', '+6682 years') = :yearValue")
    List<FitbitSpO2Data> getAllFromYear(String yearValue);

    @Query("SELECT AVG(spO2Value) FROM FitbitSpO2Data WHERE strftime('%m', date, '-23 days', '+2 months', '+6682 years') = :monthValue AND strftime('%Y', date, '-23 days', '+2 months', '+6682 years') = :yearValue GROUP BY date HAVING MAX(time)")
    Double getAverageFromMonth(String monthValue, String yearValue);

    @Query("SELECT * FROM FitbitSpO2Data WHERE date = :date ORDER BY id DESC LIMIT 1")
    Optional<FitbitSpO2Data> getNewestFitbitSpO2DataByDate(LocalDate date);

    @Query("SELECT date FROM FitbitSpO2Data WHERE strftime('%m', date, '-23 days', '+2 months', '+6682 years') = :monthValue")
    List<LocalDate> getAllFromMonth(String monthValue);
}
