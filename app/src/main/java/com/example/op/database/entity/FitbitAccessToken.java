package com.example.op.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Data;

@Entity
@Data
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
@JsonIgnoreProperties
public class FitbitAccessToken {
    @PrimaryKey(autoGenerate = true)
    public Integer id;
    public String accessToken;
    public Integer expiresIn;
    public String refreshToken;
    public String scope;
    public String tokenType;
    public String userId;
}
