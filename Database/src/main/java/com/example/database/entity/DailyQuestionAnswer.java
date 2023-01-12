package com.example.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(foreignKeys = @ForeignKey(entity = DailyQuestion.class,
        parentColumns = "id",
        childColumns = "dailyQuestionId",
        onDelete = ForeignKey.CASCADE))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyQuestionAnswer {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private Integer dailyQuestionId;
    private Integer userAnswer;
    private LocalTime timeOfAnswer;
    private LocalDate dateOfAnswer;
}
