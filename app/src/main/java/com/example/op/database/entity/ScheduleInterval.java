package com.example.op.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalTime;

import lombok.Data;

@Entity
@Data
public class ScheduleInterval {
    @PrimaryKey(autoGenerate = true)
    public Integer id;
    public LocalTime timeOfTheDay;
}
