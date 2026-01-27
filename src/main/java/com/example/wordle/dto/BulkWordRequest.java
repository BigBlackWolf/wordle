package com.example.wordle.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkWordRequest {
    @Size(min = 1, message = "At least one word pair is required")
    private List<WordPairDTO> wordPairs;
}
