package com.example.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ExpertSystemResult {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private LocalDate dateOfResult;
    private LocalTime timeOfResult;

    private Double userMovementResult;
    private Double locationResult;

    private Double fitbitStepsResult;
    private Double fitbitSpO2Result;

    private Double userFeelingsResult;

    private Double finalResult;
}
