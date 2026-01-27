package com.example.wordle.controller;

import com.example.wordle.dto.BulkWordRequest;
import com.example.wordle.dto.BulkWordResponse;
import com.example.wordle.dto.WordPairDTO;
import com.example.wordle.service.WordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/words")
@RequiredArgsConstructor
public class WordController {

    private final WordService wordService;

    @PostMapping
    public ResponseEntity<WordPairDTO> createWordPair(@Valid @RequestBody WordPairDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(wordService.createWordPair(dto));
    }

    @PostMapping("/bulk")
    public ResponseEntity<BulkWordResponse> createBulkWordPairs(
            @Valid @RequestBody BulkWordRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(wordService.createBulkWordPairs(request));
    }

    @GetMapping
    public ResponseEntity<List<WordPairDTO>> getAllWordPairs() {
        return ResponseEntity.ok(wordService.getAllWordPairs());
    }
}