package com.example.wordle.service;

import com.example.wordle.dto.QuizQuestionDTO;
import com.example.wordle.dto.QuizResultDTO;
import com.example.wordle.dto.SpellCheckRequest;
import com.example.wordle.entity.User;
import com.example.wordle.entity.WordPair;
import com.example.wordle.repository.UserRepository;
import com.example.wordle.repository.WordPairRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizService {

    private final WordPairRepository wordPairRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public QuizQuestionDTO getMultipleChoiceQuestion(String questionLanguage) {
        User user = getCurrentUser();

        long totalWords = wordPairRepository.countByUserId(user.getId());
        if (totalWords < 4) {
            throw new RuntimeException("Need at least 4 word pairs to generate a quiz");
        }

        // Get random correct word
        WordPair correctWord = wordPairRepository.findRandomWordPairByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("No words found"));

        // Get 3 random distractors
        List<WordPair> distractors = wordPairRepository.findRandomWordPairsExcluding(
                user.getId(), correctWord.getId(), 3);

        if (distractors.size() < 3) {
            throw new RuntimeException("Not enough words to generate quiz options");
        }

        List<String> options = new ArrayList<>();
        String questionWord;
        String correctAnswer;

        if ("UKRAINIAN".equalsIgnoreCase(questionLanguage)) {
            questionWord = correctWord.getUkrainianWord();
            correctAnswer = correctWord.getPolishWord();
            options.add(correctAnswer);
            distractors.forEach(wp -> options.add(wp.getPolishWord()));
        } else {
            questionWord = correctWord.getPolishWord();
            correctAnswer = correctWord.getUkrainianWord();
            options.add(correctAnswer);
            distractors.forEach(wp -> options.add(wp.getUkrainianWord()));
        }

        Collections.shuffle(options);

        return QuizQuestionDTO.builder()
                .questionWordId(correctWord.getId())
                .questionWord(questionWord)
                .questionLanguage(questionLanguage.toUpperCase())
                .options(options)
                .build();
    }

    @Transactional
    public QuizResultDTO checkSpelling(SpellCheckRequest request) {
        User user = getCurrentUser();

        String normalizedQuestion = normalizeString(request.getQuestionWord());
        String normalizedAnswer = normalizeString(request.getAnswer());

        // Find the word pair based on question language
        List<WordPair> userWords = wordPairRepository.findByUserId(user.getId());

        WordPair wordPair = userWords.stream()
                .filter(wp -> {
                    if ("UKRAINIAN".equalsIgnoreCase(request.getQuestionLanguage())) {
                        return normalizeString(wp.getUkrainianWord()).equals(normalizedQuestion);
                    } else {
                        return normalizeString(wp.getPolishWord()).equals(normalizedQuestion);
                    }
                })
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Word not found"));

        String correctAnswer;
        if ("UKRAINIAN".equalsIgnoreCase(request.getQuestionLanguage())) {
            correctAnswer = wordPair.getPolishWord();
        } else {
            correctAnswer = wordPair.getUkrainianWord();
        }

        boolean isCorrect = normalizedAnswer.equals(normalizeString(correctAnswer));

        // Update statistics
        if (isCorrect) {
            wordPair.setCorrectCount(wordPair.getCorrectCount() + 1);
        } else {
            wordPair.setIncorrectCount(wordPair.getIncorrectCount() + 1);
        }
        wordPairRepository.save(wordPair);

        return QuizResultDTO.builder()
                .correct(isCorrect)
                .correctAnswer(correctAnswer)
                .providedAnswer(request.getAnswer())
                .message(isCorrect ? "Correct!" : "Incorrect. Try again!")
                .build();
    }

    private String normalizeString(String str) {
        return str.trim().toLowerCase();
    }
}