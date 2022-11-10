package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.database.entity.ControlTextUserAnswer;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Dao
public interface ControlTextUserAnswerDao {
    @Query("SELECT * FROM ControlTextUserAnswer WHERE id = :id")
    Optional<ControlTextUserAnswer> getById(Integer id);

    @Query("SELECT * FROM ControlTextUserAnswer WHERE dateOfAnswer = :date")
    Optional<ControlTextUserAnswer> getByDate(LocalDate date);

    @Query("SELECT COUNT(id) FROM ControlTextUserAnswer;")
    Optional<Integer> getAllCount();

    @Query("SELECT * FROM ControlTextUserAnswer;")
    List<ControlTextUserAnswer> getAllAns();

    @Query("SELECT COUNT(*) " +
           "FROM ControlTextUserAnswer, ControlTextQuestion " +
           "WHERE ControlTextUserAnswer.controlTextQuestionId = ControlTextQuestion.id " +
           "AND ControlTextUserAnswer.userAnswer = ControlTextQuestion.correctAnswer;")
    Optional<Integer> getAllCorrectCount();

    @Query("SELECT COUNT(*) " +
           "FROM ControlTextUserAnswer, ControlTextQuestion " +
           "WHERE ControlTextUserAnswer.controlTextQuestionId = ControlTextQuestion.id " +
           "AND ControlTextUserAnswer.userAnswer != ControlTextQuestion.correctAnswer;")
    Optional<Integer> getAllIncorrectCount();

    @Insert
    void insert(ControlTextUserAnswer controlTextUserAnswer);
}
