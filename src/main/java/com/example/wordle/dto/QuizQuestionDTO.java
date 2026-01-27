package com.example.wordle.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Quiz DTOs
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizQuestionDTO {
    private Long questionWordId;
    private String questionWord;
    private String questionLanguage; // "POLISH" or "UKRAINIAN"
    private List<String> options;
}
