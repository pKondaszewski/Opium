package com.example.database;

import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FitbitData {
    @EqualsAndHashCode.Exclude
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private LocalDate date;
    @EqualsAndHashCode.Exclude
    private LocalTime time;

    public FitbitData(Integer id, LocalDate date, LocalTime time) {
        this.id = id;
        this.date = date;
        this.time = time;
    }
}

