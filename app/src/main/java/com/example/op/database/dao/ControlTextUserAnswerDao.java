package com.example.op.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.op.database.entity.ControlTextUserAnswer;

import java.util.Optional;

@Dao
public interface ControlTextUserAnswerDao {
    @Query("SELECT * FROM ControlTextUserAnswer WHERE id = :id")
    Optional<ControlTextUserAnswer> getById(Integer id);

    @Query("SELECT * FROM ControlTextUserAnswer WHERE dateOfAnswer = :date")
    Optional<ControlTextUserAnswer> getByDate(String date);

    @Insert
    void insert(ControlTextUserAnswer controlTextUserAnswer);
}
