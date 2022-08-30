package com.example.op.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private String mood = "-";
    private List<String> ailments = List.of("-");
    private Integer sleepQuality = -1;
    private String note = "-";
    private LocalDate dateOfDailyFeelings;
}
