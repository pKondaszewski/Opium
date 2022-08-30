package com.example.op.database.entity;

import android.location.Location;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.op.utils.UserAddress;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhoneLocalization {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private LocalTime localizationCheckTime;
    private LocalDate localizationCheckDate;
    private double latitude;
    private double longitude;
    private UserAddress userAddress;
}
