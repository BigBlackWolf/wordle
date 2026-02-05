package com.example.wordle.service;

import com.example.wordle.dto.BulkWordRequest;
import com.example.wordle.dto.BulkWordResponse;
import com.example.wordle.dto.WordPairDTO;
import com.example.wordle.entity.User;
import com.example.wordle.entity.WordPair;
import com.example.wordle.repository.UserRepository;
import com.example.wordle.repository.WordPairRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WordServiceTest {

    @Mock
    private WordPairRepository wordPairRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private WordService wordService;

    @Test
    void shouldCreateWordPair() {
        // Given
        User user = User.builder()
                .username("john")
                .email("john@example.com")
                .password("asd123F")
                .build();
        WordPairDTO dto = WordPairDTO.builder()
                .polishWord("kot")
                .ukrainianWord("кіт")
                .build();

        WordPair savedWordPair = WordPair.builder()
                .polishWord("kot")
                .ukrainianWord("кіт")
                .user(user)
                .correctCount(0)
                .incorrectCount(0)
                .build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(wordPairRepository.save(any(WordPair.class))).thenReturn(savedWordPair);

        // When
        WordPairDTO result = wordService.createWordPair(dto);

        // Then
        assertThat(result.getPolishWord()).isEqualTo("kot");
        assertThat(result.getUkrainianWord()).isEqualTo("кіт");
        verify(wordPairRepository).save(any(WordPair.class));
    }

    @Test
    void shouldTrimWhitespaceWhenCreatingWordPair() {
        // Given
        User user = User.builder()
                .username("john")
                .email("john@example.com")
                .password("asd123F")
                .build();
        WordPairDTO dto = WordPairDTO.builder()
                .polishWord("  kot  ")
                .ukrainianWord("  кіт  ")
                .build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(wordPairRepository.save(any(WordPair.class))).thenAnswer(i -> i.getArguments()[0]);

        // When
        wordService.createWordPair(dto);

        // Then
        verify(wordPairRepository).save(argThat(wp ->
                wp.getPolishWord().equals("kot") &&
                        wp.getUkrainianWord().equals("кіт")
        ));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundForWordCreation() {
        // Given
        WordPairDTO dto = WordPairDTO.builder()
                .polishWord("kot")
                .ukrainianWord("кіт")
                .build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> wordService.createWordPair(dto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("User not found");
    }

    @Test
    void shouldCreateBulkWordPairs() {
        // Given
        User user = User.builder()
                .username("john")
                .email("john@example.com")
                .password("asd123F")
                .build();

        List<WordPairDTO> dtos = Arrays.asList(
                WordPairDTO.builder().polishWord("kot").ukrainianWord("кіт").build(),
                WordPairDTO.builder().polishWord("pies").ukrainianWord("собака").build()
        );

        BulkWordRequest request = BulkWordRequest.builder()
                .wordPairs(dtos)
                .build();

        WordPair wp1 = WordPair.builder()
                .id(1L)
                .polishWord("kot")
                .ukrainianWord("кіт")
                .user(user)
                .correctCount(0)
                .incorrectCount(0)
                .build();

        WordPair wp2 = WordPair.builder()
                .id(2L)
                .polishWord("pies")
                .ukrainianWord("собака")
                .user(user)
                .correctCount(0)
                .incorrectCount(0)
                .build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(wordPairRepository.saveAll(anyList())).thenReturn(Arrays.asList(wp1, wp2));

        // When
        BulkWordResponse response = wordService.createBulkWordPairs(request);

        // Then
        assertThat(response.getTotalProcessed()).isEqualTo(2);
        assertThat(response.getCreatedWords()).hasSize(2);
        assertThat(response.getCreatedWords()).extracting(WordPairDTO::getPolishWord)
                .containsExactly("kot", "pies");
    }

    @Test
    void shouldGetAllWordPairsForUser() {
        // Given
        User user = User.builder().id(1L).build();

        WordPair wp1 = WordPair.builder()
                .id(1L)
                .polishWord("kot")
                .ukrainianWord("кіт")
                .user(user)
                .correctCount(5)
                .incorrectCount(2)
                .build();

        WordPair wp2 = WordPair.builder()
                .id(2L)
                .polishWord("pies")
                .ukrainianWord("собака")
                .user(user)
                .correctCount(3)
                .incorrectCount(1)
                .build();

        Authentication authentication = Mockito.mock(Authentication.class);
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(SecurityContextHolder.getContext().getAuthentication().getName()).thenReturn("john");
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));
        when(wordPairRepository.findByUserId(1L)).thenReturn(Arrays.asList(wp1, wp2));

        // When
        List<WordPairDTO> results = wordService.getAllWordPairs();

        // Then
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getCorrectCount()).isEqualTo(5);
        assertThat(results.get(0).getIncorrectCount()).isEqualTo(2);
    }
}