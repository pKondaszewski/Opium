package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.database.entity.PhoneMovement;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
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

    @Query("SELECT timeOfMovement FROM PhoneMovement WHERE dateOfMovement = :date ORDER BY id DESC LIMIT 1")
    Optional<LocalTime> getNewestPhoneMovementByDate(LocalDate date);
}
