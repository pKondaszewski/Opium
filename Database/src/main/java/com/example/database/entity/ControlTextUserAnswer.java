package com.example.database.entity;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(foreignKeys = @ForeignKey(entity = ControlTextQuestion.class,
        parentColumns = "id",
        childColumns = "controlTextQuestionId",
        onDelete = ForeignKey.CASCADE))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlTextUserAnswer {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private Integer controlTextQuestionId;
    private Integer userAnswer;
    private LocalTime timeOfAnswer;
    private LocalDate dateOfAnswer;
}
