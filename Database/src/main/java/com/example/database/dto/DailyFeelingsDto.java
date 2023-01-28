package com.example.database.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyFeelingsDto {
    private String mood;
    private LocalDate dateOfDailyFeelings;
}
