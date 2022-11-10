package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.database.entity.ControlTextQuestion;

import java.util.List;
import java.util.Optional;

@Dao
public interface ControlTextQuestionDao extends CrudDao<ControlTextQuestion> {
    @Query("SELECT * FROM ControlTextQuestion")
    List<ControlTextQuestion> getAll();

    @Query("SELECT COUNT(id) FROM ControlTextQuestion;")
    Integer getAllCount();

    @Query("SELECT * FROM ControlTextQuestion WHERE id = :id")
    Optional<ControlTextQuestion> getById(Integer id);
}
