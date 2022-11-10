package com.example.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FitbitSpO2Data {
    @EqualsAndHashCode.Exclude
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private LocalDate date;
    @EqualsAndHashCode.Exclude
    private LocalTime time;
    private String spO2Value = "-";
}