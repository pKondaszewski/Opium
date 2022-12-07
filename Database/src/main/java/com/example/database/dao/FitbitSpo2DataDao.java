package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.database.entity.FitbitSpO2Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Dao
public interface FitbitSpo2DataDao extends CrudDao<FitbitSpO2Data> {
    @Query("SELECT * FROM FitbitSpO2Data")
    List<FitbitSpO2Data> getAll();

    @Query("SELECT * FROM FitbitSpO2Data WHERE spO2Value NOT NULL GROUP BY date HAVING MAX(time)")
    List<FitbitSpO2Data> getHighestFitbitSpO2DataValueByEveryDate();

    @Query("SELECT * FROM FitbitSpO2Data WHERE date >= :startDate AND date <= :endDate GROUP BY date HAVING MAX(time)")
    List<FitbitSpO2Data> getAllBetweenTwoDates(LocalDate startDate, LocalDate endDate);

    @Query("SELECT AVG(spO2Value) FROM FitbitSpO2Data WHERE date >= :startDate AND date <= :endDate GROUP BY date HAVING MAX(time)")
    Double getAverageBetweenTwoDates(LocalDate startDate, LocalDate endDate);

    @Query("SELECT * FROM FitbitSpO2Data WHERE date = :date ORDER BY id DESC LIMIT 1")
    Optional<FitbitSpO2Data> getNewestFitbitSpO2DataByDate(LocalDate date);
}
