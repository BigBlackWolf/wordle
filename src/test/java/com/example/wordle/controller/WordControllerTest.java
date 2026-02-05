package com.example.wordle.controller;

import com.example.wordle.dto.BulkWordRequest;
import com.example.wordle.dto.BulkWordResponse;
import com.example.wordle.dto.WordPairDTO;
import com.example.wordle.security.JwtAuthenticationFilter;
import com.example.wordle.service.WordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WordController.class)
@AutoConfigureMockMvc(addFilters = false)
class WordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private WordService wordService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthFilter;

    @MockitoBean
    private UserDetailsService userDetailsService;

    @Test
    void shouldCreateWordPair() throws Exception {
        // Given
        WordPairDTO inputDto = WordPairDTO.builder()
                .polishWord("kot")
                .ukrainianWord("кіт")
                .build();

        WordPairDTO responseDto = WordPairDTO.builder()
                .id(1L)
                .polishWord("kot")
                .ukrainianWord("кіт")
                .correctCount(0)
                .incorrectCount(0)
                .build();

        when(wordService.createWordPair(any(WordPairDTO.class))).thenReturn(responseDto);

        // When & Then
        mockMvc.perform(post("/api/words")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.polishWord").value("kot"))
                .andExpect(jsonPath("$.ukrainianWord").value("кіт"));
    }

    @Test
    void shouldReturnBadRequestWhenPolishWordIsBlank() throws Exception {
        // Given
        WordPairDTO dto = WordPairDTO.builder()
                .polishWord("")
                .ukrainianWord("кіт")
                .build();

        // When & Then
        mockMvc.perform(post("/api/words")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldCreateBulkWordPairs() throws Exception {
        // Given
        List<WordPairDTO> wordPairs = Arrays.asList(
                WordPairDTO.builder().polishWord("kot").ukrainianWord("кіт").build(),
                WordPairDTO.builder().polishWord("pies").ukrainianWord("собака").build()
        );

        BulkWordRequest request = BulkWordRequest.builder()
                .wordPairs(wordPairs)
                .build();

        List<WordPairDTO> createdWords = Arrays.asList(
                WordPairDTO.builder()
                        .id(1L)
                        .polishWord("kot")
                        .ukrainianWord("кіт")
                        .correctCount(0)
                        .incorrectCount(0)
                        .build(),
                WordPairDTO.builder()
                        .id(2L)
                        .polishWord("pies")
                        .ukrainianWord("собака")
                        .correctCount(0)
                        .incorrectCount(0)
                        .build()
        );

        BulkWordResponse response = BulkWordResponse.builder()
                .totalProcessed(2)
                .createdWords(createdWords)
                .build();

        when(wordService.createBulkWordPairs(any(BulkWordRequest.class)))
                .thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/words/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalProcessed").value(2))
                .andExpect(jsonPath("$.createdWords", hasSize(2)))
                .andExpect(jsonPath("$.createdWords[0].polishWord").value("kot"))
                .andExpect(jsonPath("$.createdWords[1].polishWord").value("pies"));
    }

    @Test
    void shouldGetAllWordPairs() throws Exception {
        // Given
        List<WordPairDTO> words = Arrays.asList(
                WordPairDTO.builder()
                        .id(1L)
                        .polishWord("kot")
                        .ukrainianWord("кіт")
                        .correctCount(5)
                        .incorrectCount(2)
                        .build(),
                WordPairDTO.builder()
                        .id(2L)
                        .polishWord("pies")
                        .ukrainianWord("собака")
                        .correctCount(3)
                        .incorrectCount(1)
                        .build()
        );

        when(wordService.getAllWordPairs()).thenReturn(words);

        // When & Then
        mockMvc.perform(get("/api/words"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].polishWord").value("kot"))
                .andExpect(jsonPath("$[0].correctCount").value(5))
                .andExpect(jsonPath("$[1].polishWord").value("pies"));
    }
}