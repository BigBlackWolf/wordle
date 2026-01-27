package com.example.wordle.controller;

import com.example.wordle.dto.QuizQuestionDTO;
import com.example.wordle.dto.QuizResultDTO;
import com.example.wordle.dto.SpellCheckRequest;
import com.example.wordle.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/quiz")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/multiple-choice")
    public ResponseEntity<QuizQuestionDTO> getMultipleChoiceQuestion(
            @RequestParam(defaultValue = "UKRAINIAN") String questionLanguage) {
        return ResponseEntity.ok(quizService.getMultipleChoiceQuestion(questionLanguage));
    }

    @PostMapping("/spell-check")
    public ResponseEntity<QuizResultDTO> checkSpelling(
            @Valid @RequestBody SpellCheckRequest request) {
        return ResponseEntity.ok(quizService.checkSpelling(request));
    }
}