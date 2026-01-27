package com.example.wordle.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpellCheckRequest {
    @NotBlank(message = "Question word is required")
    private String questionWord;

    @NotBlank(message = "Question language is required")
    private String questionLanguage; // "POLISH" or "UKRAINIAN"

    @NotBlank(message = "Answer is required")
    private String answer;
}
