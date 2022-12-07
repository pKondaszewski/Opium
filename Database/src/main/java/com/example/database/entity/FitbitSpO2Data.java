package com.example.database.entity;

import androidx.room.Entity;

import com.example.database.FitbitData;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@NoArgsConstructor
public class FitbitSpO2Data extends FitbitData {
    @Getter
    @Setter
    private String spO2Value = "-";
    public FitbitSpO2Data(Integer id, LocalDate date, LocalTime time, String spO2Value) {
        super(id, date, time);
        this.spO2Value = spO2Value;
    }
}
