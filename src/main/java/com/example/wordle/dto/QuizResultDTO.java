package com.example.wordle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResultDTO {
    private boolean correct;
    private String correctAnswer;
    private String providedAnswer;
    private String message;
}
