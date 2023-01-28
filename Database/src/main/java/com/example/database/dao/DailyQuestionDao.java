package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.database.entity.DailyQuestion;

import java.util.List;
import java.util.Optional;

@Dao
public interface DailyQuestionDao extends CrudDao<DailyQuestion> {
    @Query("SELECT * FROM DailyQuestion")
    List<DailyQuestion> getAll();

    @Query("SELECT COUNT(id) FROM DailyQuestion;")
    Integer getAllCount();

    @Insert
    void insertAll(DailyQuestion... DailyQuestions);

    @Query("SELECT * FROM DailyQuestion WHERE id = :id")
    Optional<DailyQuestion> getById(Integer id);

    @Query("SELECT textQuestion FROM DailyQuestion WHERE id = :id")
    Optional<String> getTextQuestionById(Integer id);
}
