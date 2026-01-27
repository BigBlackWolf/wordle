package com.example.wordle.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "word_pairs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordPair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String polishWord;

    @Column(nullable = false)
    private String ukrainianWord;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private Integer correctCount;

    @Column(nullable = false)
    private Integer incorrectCount;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (correctCount == null) correctCount = 0;
        if (incorrectCount == null) incorrectCount = 0;
    }
}