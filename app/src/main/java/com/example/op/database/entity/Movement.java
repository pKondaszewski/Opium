package com.example.op.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Movement {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private LocalTime timeOfMovement;
    private LocalDate dateOfMovement;
}
