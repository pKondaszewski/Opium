package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.MapInfo;
import androidx.room.Query;

import com.example.database.entity.FitbitStepsData;
import com.example.database.entity.PhoneLocalization;
import com.example.database.entity.PhoneMovement;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Dao
public interface PhoneMovementDao extends CrudDao<PhoneMovement> {
    @Query("SELECT * FROM PhoneMovement")
    List<PhoneMovement> getAll();

    @Query("SELECT COUNT(*) FROM PhoneMovement WHERE dateOfMovement = :date")
    Integer getCountByDate(LocalDate date);

    @Query("SELECT timeOfMovement FROM PhoneMovement ORDER BY timeOfMovement ASC")
    List<LocalTime> getAllTime();

    @Query("SELECT timeOfMovement FROM PhoneMovement WHERE dateOfMovement = :date")
    List<LocalTime> getAllTimeByDate(LocalDate date);

    @MapInfo(keyColumn = "dateOfMovement", valueColumn = "count")
    @Query("SELECT dateOfMovement, COUNT(*) as count FROM PhoneMovement GROUP BY dateOfMovement ORDER BY dateOfMovement ASC LIMIT :dayLimit;")
    Map<LocalDate, Integer> getMovementsCountByEveryDate(int dayLimit);

    @Query("SELECT timeOfMovement FROM PhoneMovement WHERE dateOfMovement = :date ORDER BY id DESC LIMIT 1")
    Optional<LocalTime> getNewestPhoneMovementByDate(LocalDate date);
}
