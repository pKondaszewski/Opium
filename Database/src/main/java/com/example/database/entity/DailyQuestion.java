package com.example.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyQuestion {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String textQuestion;
    private Integer correctAnswer;
}
