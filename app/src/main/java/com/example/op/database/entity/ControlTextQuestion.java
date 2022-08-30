package com.example.op.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ControlTextQuestion {
    @PrimaryKey(autoGenerate = true)
    public Integer id;
    public String textQuestion;
    public Integer correctAnswer;
}
