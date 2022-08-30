package com.example.op.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.op.database.entity.ControlTextQuestion;

import java.util.List;

@Dao
public interface ControlTextQuestionDao extends CrudDao<ControlTextQuestion> {
    @Query("SELECT * FROM ControlTextQuestion")
    List<ControlTextQuestion> getAll();

    @Query("SELECT * FROM ControlTextQuestion WHERE id = :id")
    ControlTextQuestion getById(Integer id);
}
