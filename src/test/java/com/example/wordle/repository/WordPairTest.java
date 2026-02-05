package com.example.wordle.repository;

import com.example.wordle.entity.User;
import com.example.wordle.entity.WordPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class WordPairRepositoryTest {

    @Autowired
    private WordPairRepository wordPairRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("asd123F")
                .build();
        testUser = userRepository.save(testUser);
    }

    @Test
    void shouldSaveWordPair() {
        // Given
        WordPair wordPair = WordPair.builder()
                .polishWord("kot")
                .ukrainianWord("кіт")
                .user(testUser)
                .build();

        // When
        WordPair saved = wordPairRepository.save(wordPair);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPolishWord()).isEqualTo("kot");
        assertThat(saved.getUkrainianWord()).isEqualTo("кіт");
        assertThat(saved.getCorrectCount()).isEqualTo(0);
        assertThat(saved.getIncorrectCount()).isEqualTo(0);
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindWordPairsByUserId() {
        // Given
        WordPair wp1 = WordPair.builder()
                .polishWord("kot")
                .ukrainianWord("кіт")
                .user(testUser)
                .build();
        WordPair wp2 = WordPair.builder()
                .polishWord("pies")
                .ukrainianWord("собака")
                .user(testUser)
                .build();
        wordPairRepository.save(wp1);
        wordPairRepository.save(wp2);

        // When
        List<WordPair> words = wordPairRepository.findByUserId(testUser.getId());

        // Then
        assertThat(words).hasSize(2);
        assertThat(words).extracting(WordPair::getPolishWord)
                .containsExactlyInAnyOrder("kot", "pies");
    }

    @Test
    void shouldCountWordPairsByUserId() {
        // Given
        for (int i = 0; i < 5; i++) {
            WordPair wp = WordPair.builder()
                    .polishWord("word" + i)
                    .ukrainianWord("слово" + i)
                    .user(testUser)
                    .build();
            wordPairRepository.save(wp);
        }

        // When
        long count = wordPairRepository.countByUserId(testUser.getId());

        // Then
        assertThat(count).isEqualTo(5);
    }

    @Test
    void shouldFindRandomWordPair() {
        // Given
        WordPair wp1 = WordPair.builder()
                .polishWord("kot")
                .ukrainianWord("кіт")
                .user(testUser)
                .build();
        WordPair wp2 = WordPair.builder()
                .polishWord("pies")
                .ukrainianWord("собака")
                .user(testUser)
                .build();
        wordPairRepository.save(wp1);
        wordPairRepository.save(wp2);

        // When
        Optional<WordPair> random = wordPairRepository.findRandomWordPairByUserId(testUser.getId());

        // Then
        assertThat(random).isPresent();
        assertThat(random.get().getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    void shouldFindRandomWordPairsExcludingOne() {
        // Given
        WordPair wp1 = WordPair.builder()
                .polishWord("kot")
                .ukrainianWord("кіт")
                .user(testUser)
                .build();
        WordPair wp2 = WordPair.builder()
                .polishWord("pies")
                .ukrainianWord("собака")
                .user(testUser)
                .build();
        WordPair wp3 = WordPair.builder()
                .polishWord("dom")
                .ukrainianWord("будинок")
                .user(testUser)
                .build();
        WordPair wp4 = WordPair.builder()
                .polishWord("woda")
                .ukrainianWord("вода")
                .user(testUser)
                .build();

        wp1 = wordPairRepository.save(wp1);
        wordPairRepository.save(wp2);
        wordPairRepository.save(wp3);
        wordPairRepository.save(wp4);

        // When
        List<WordPair> randoms = wordPairRepository.findRandomWordPairsExcluding(
                testUser.getId(), wp1.getId(), 2);

        // Then
        assertThat(randoms).hasSize(2);
        assertThat(randoms).extracting(WordPair::getId)
                .doesNotContain(wp1.getId());
    }

    @Test
    void shouldOnlyReturnWordPairsForSpecificUser() {
        // Given
        User anotherUser = User.builder()
                .username("another")
                .email("another@example.com")
                .password("asd123F")
                .build();
        anotherUser = userRepository.save(anotherUser);

        WordPair wp1 = WordPair.builder()
                .polishWord("kot")
                .ukrainianWord("кіт")
                .user(testUser)
                .build();
        WordPair wp2 = WordPair.builder()
                .polishWord("pies")
                .ukrainianWord("собака")
                .user(anotherUser)
                .build();
        wordPairRepository.save(wp1);
        wordPairRepository.save(wp2);

        // When
        List<WordPair> testUserWords = wordPairRepository.findByUserId(testUser.getId());

        // Then
        assertThat(testUserWords).hasSize(1);
        assertThat(testUserWords.get(0).getPolishWord()).isEqualTo("kot");
    }
}