package com.example.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.database.HomeAddress;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Profile {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private String firstname;
    private String lastname;
    private LocalDate birthdate;
    private String gender;
    private String phoneNumber;
    private String emailAddress;
    private HomeAddress homeAddress;
}
