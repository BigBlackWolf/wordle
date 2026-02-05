package com.example.wordle.repository;

import com.example.wordle.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@TestPropertySource(locations = "classpath:application-test.properties")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUser() {
        // Given
        User user = User.builder()
                .username("testuser")
                .email("test@example.com")
                .password("asd123F")
                .build();

        // When
        User saved = userRepository.save(user);

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getUsername()).isEqualTo("testuser");
        assertThat(saved.getEmail()).isEqualTo("test@example.com");
        assertThat(saved.getPassword()).isEqualTo("asd123F");
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldFindUserByUsername() {
        // Given
        User user = User.builder()
                .username("john")
                .email("john@example.com")
                .password("asd123F")
                .build();
        userRepository.save(user);

        // When
        Optional<User> found = userRepository.findByUsername("john");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("john");
    }

    @Test
    void shouldFindUserByEmail() {
        // Given
        User user = User.builder()
                .username("jane")
                .email("jane@example.com")
                .password("asd123F")
                .build();
        userRepository.save(user);

        // When
        Optional<User> found = userRepository.findByEmail("jane@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getEmail()).isEqualTo("jane@example.com");
    }

    @Test
    void shouldReturnTrueIfUsernameExists() {
        // Given
        User user = User.builder()
                .username("existing")
                .email("existing@example.com")
                .password("asd123F")
                .build();
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByUsername("existing");

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseIfUsernameDoesNotExist() {
        // When
        boolean exists = userRepository.existsByUsername("nonexistent");

        // Then
        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnTrueIfEmailExists() {
        // Given
        User user = User.builder()
                .username("user")
                .email("exists@example.com")
                .password("asd123F")
                .build();
        userRepository.save(user);

        // When
        boolean exists = userRepository.existsByEmail("exists@example.com");

        // Then
        assertThat(exists).isTrue();
    }
}