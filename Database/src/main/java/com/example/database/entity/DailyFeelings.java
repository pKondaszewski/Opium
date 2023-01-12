package com.example.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyFeelings {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String mood;
    private List<String> ailments;
    private String otherAilments;
    private String note;
    private LocalDate dateOfDailyFeelings;
    private LocalTime timeOfDailyFeelings;

    public DailyFeelings(LocalDate dateOfDailyFeelings) {
        this.dateOfDailyFeelings = dateOfDailyFeelings;
    }
}
