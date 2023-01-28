package com.example.database.dto;

import java.time.LocalTime;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DailyQuestionAnswerDto {
    private Integer dailyQuestionId;
    private Integer userAnswer;
    private LocalTime timeOfAnswer;
}
