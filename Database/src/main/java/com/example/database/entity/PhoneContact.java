package com.example.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@Entity
@AllArgsConstructor
public class PhoneContact {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String name;
    private String phoneNumber;
    private boolean isFromContactBook;
}
