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

    @Query("SELECT * FROM FitbitStepsData WHERE date GROUP BY date HAVING MAX(time) BETWEEN :startDate AND :endDate")
    List<FitbitStepsData> getAllBetweenTwoDates(String startDate, String endDate);

    @Query("SELECT * FROM FitbitStepsData WHERE date = :date ORDER BY id DESC LIMIT 1")
    Optional<FitbitStepsData> getNewestFitbitStepsDataByDate(LocalDate date);
}
