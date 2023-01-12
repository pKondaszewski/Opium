package com.example.database.dao;

import androidx.room.Dao;
import androidx.room.Query;

import com.example.database.entity.DailyQuestionAnswer;
import com.example.database.dto.DailyQuestionAnswerDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Dao
public interface DailyQuestionAnswerDao extends CrudDao<DailyQuestionAnswer> {
    @Query("SELECT * FROM DailyQuestionAnswer WHERE id = :id")
    Optional<DailyQuestionAnswer> getById(Integer id);

    @Query("SELECT * FROM DailyQuestionAnswer WHERE dateOfAnswer = :date ORDER BY timeOfAnswer DESC LIMIT 1")
    Optional<DailyQuestionAnswer> getNewestByDate(LocalDate date);

    @Query("SELECT dailyQuestionId, userAnswer FROM DailyQuestionAnswer WHERE dateOfAnswer = :date ORDER BY timeOfAnswer DESC LIMIT 1")
    Optional<DailyQuestionAnswerDto> getNewestIdByDate(LocalDate date);

    @Query("SELECT dailyQuestionId, userAnswer, timeOfAnswer FROM DailyQuestionAnswer ORDER BY id DESC LIMIT 1")
    Optional<DailyQuestionAnswerDto> getNewestId();

    @Query("SELECT COUNT(id) FROM DailyQuestionAnswer;")
    Optional<Integer> getAllCount();

    @Query("SELECT * FROM DailyQuestionAnswer;")
    List<DailyQuestionAnswer> getAllAns();

    @Query("SELECT COUNT(*) " +
           "FROM DailyQuestionAnswer, DailyQuestion " +
           "WHERE DailyQuestionAnswer.dailyQuestionId = DailyQuestion.id " +
           "AND DailyQuestionAnswer.userAnswer = DailyQuestion.correctAnswer;")
    Optional<Integer> getAllCorrectCount();

    @Query("SELECT COUNT(*) " +
           "FROM DailyQuestionAnswer, DailyQuestion " +
           "WHERE DailyQuestionAnswer.dailyQuestionId = DailyQuestion.id " +
           "AND DailyQuestionAnswer.userAnswer != DailyQuestion.correctAnswer;")
    Optional<Integer> getAllIncorrectCount();

    @Query("SELECT dateOfAnswer FROM DailyQuestionAnswer WHERE strftime('%m', dateOfAnswer, '-23 days', '+2 months', '+6682 years') = :monthValue AND strftime('%Y', dateOfAnswer, '-23 days', '+2 months', '+6682 years') = :yearValue")
    List<LocalDate> getAllFromMonth(String monthValue, String yearValue);
}
