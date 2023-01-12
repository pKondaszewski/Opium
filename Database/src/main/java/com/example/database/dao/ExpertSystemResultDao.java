package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.database.entity.EmailContact;
import com.example.database.entity.ExpertSystemResult;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Dao
public interface ExpertSystemResultDao extends CrudDao<ExpertSystemResult> {
    @Query("SELECT * from ExpertSystemResult")
    List<ExpertSystemResult> getAll();

    @Query("SELECT * FROM ExpertSystemResult WHERE dateOfResult = :localDate")
    Optional<ExpertSystemResult> getByDate(LocalDate localDate);
}
