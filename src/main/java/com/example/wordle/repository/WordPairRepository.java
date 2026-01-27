package com.example.wordle.repository;

import com.example.wordle.entity.WordPair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WordPairRepository extends JpaRepository<WordPair, Long> {

    List<WordPair> findByUserId(Long userId);

    long countByUserId(Long userId);

    @Query(value = "SELECT * FROM word_pairs WHERE user_id = :userId ORDER BY RANDOM() LIMIT 1",
            nativeQuery = true)
    Optional<WordPair> findRandomWordPairByUserId(@Param("userId") Long userId);

    @Query(value = "SELECT * FROM word_pairs WHERE user_id = :userId AND id != :excludeId ORDER BY RANDOM() LIMIT :limit",
            nativeQuery = true)
    List<WordPair> findRandomWordPairsExcluding(
            @Param("userId") Long userId,
            @Param("excludeId") Long excludeId,
            @Param("limit") int limit
    );
}