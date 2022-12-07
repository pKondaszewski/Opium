package com.example.database.entity;

import androidx.room.Entity;

import com.example.database.FitbitData;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
public class FitbitStepsData extends FitbitData {
    @Getter
    @Setter
    private String stepsValue = "-";

    public FitbitStepsData(Integer id, LocalDate date, LocalTime time, String stepsValue) {
        super(id, date, time);
        this.stepsValue = stepsValue;
    }
}
