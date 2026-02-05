package com.example.wordle.service;

import com.example.wordle.dto.BulkWordRequest;
import com.example.wordle.dto.BulkWordResponse;
import com.example.wordle.dto.WordPairDTO;
import com.example.wordle.entity.User;
import com.example.wordle.entity.WordPair;
import com.example.wordle.repository.UserRepository;
import com.example.wordle.repository.WordPairRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class WordService {

    private final WordPairRepository wordPairRepository;
    private final UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info(String.format("Found user %s", username));
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public WordPairDTO createWordPair(WordPairDTO dto) {
        User user = getCurrentUser();

        WordPair wordPair = WordPair.builder()
                .polishWord(dto.getPolishWord().trim())
                .ukrainianWord(dto.getUkrainianWord().trim())
                .user(user)
                .build();

        WordPair saved = wordPairRepository.save(wordPair);
        return convertToDTO(saved);
    }

    @Transactional
    public BulkWordResponse createBulkWordPairs(BulkWordRequest request) {
        User user = getCurrentUser();

        List<WordPair> wordPairs = request.getWordPairs().stream()
                .map(dto -> WordPair.builder()
                        .polishWord(dto.getPolishWord().trim())
                        .ukrainianWord(dto.getUkrainianWord().trim())
                        .user(user)
                        .build())
                .collect(Collectors.toList());

        List<WordPair> savedPairs = wordPairRepository.saveAll(wordPairs);

        return BulkWordResponse.builder()
                .totalProcessed(savedPairs.size())
                .createdWords(savedPairs.stream()
                        .map(this::convertToDTO)
                        .collect(Collectors.toList()))
                .build();
    }

    public List<WordPairDTO> getAllWordPairs() {
        User user = getCurrentUser();
        return wordPairRepository.findByUserId(user.getId()).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private WordPairDTO convertToDTO(WordPair wordPair) {
        return WordPairDTO.builder()
                .id(wordPair.getId())
                .polishWord(wordPair.getPolishWord())
                .ukrainianWord(wordPair.getUkrainianWord())
                .correctCount(wordPair.getCorrectCount())
                .incorrectCount(wordPair.getIncorrectCount())
                .build();
    }
}