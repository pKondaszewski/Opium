package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.database.entity.ExpertSystemResult;

import java.time.LocalDate;
import java.util.Optional;

@Dao
public interface ExpertSystemResultDao extends CrudDao<ExpertSystemResult> {
    @Query("SELECT * FROM ExpertSystemResult WHERE dateOfResult = :localDate ORDER BY timeOfResult DESC LIMIT 1")
    Optional<ExpertSystemResult> getNewestByDate(LocalDate localDate);
}
