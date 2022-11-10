package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.database.entity.DailyFeelings;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Dao
public interface DailyFeelingsDao extends CrudDao<DailyFeelings> {

    @Query("SELECT * FROM DailyFeelings")
    List<DailyFeelings> getAll();

    @Query("SELECT * FROM DailyFeelings WHERE id = :id")
    Optional<DailyFeelings> getById(int id);

    @Query("SELECT * FROM DailyFeelings WHERE dateOfDailyFeelings = :localDate")
    Optional<DailyFeelings> getByDate(LocalDate localDate);
}
