package com.ryanmovie.repository;

import com.ryanmovie.model.entity.CategoryEnum;
import com.ryanmovie.model.entity.MovieModel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<MovieModel, Long> {
    @Query("""
        SELECT m FROM MovieModel m
        LEFT JOIN FETCH m.categories
        WHERE m.id = :id
    """)
    Optional<MovieModel> findMovieByIdWithCategories(@Param("id") Long id);

    @Query("""
        SELECT DISTINCT m FROM MovieModel m
        JOIN FETCH m.categories c
        WHERE c.id = :categoryId
        ORDER BY m.rateScore DESC
    """)
    List<MovieModel> findTopByCategoryOrderByRateScoreDesc(
            @Param("categoryId") Long categoryId,
            Pageable pageable
    );
}
