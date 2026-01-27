package com.example.wordle.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Word DTOs
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordPairDTO {
    private Long id;

    @NotBlank(message = "Polish word is required")
    private String polishWord;

    @NotBlank(message = "Ukrainian word is required")
    private String ukrainianWord;

    private Integer correctCount;
    private Integer incorrectCount;
}
