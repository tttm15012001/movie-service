package com.ryanmovie.repository;

import com.ryanmovie.model.entity.MovieModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<MovieModel, Long> {
    @Query("""
        SELECT m FROM MovieModel m
        LEFT JOIN FETCH m.categories
        WHERE m.id = :id
    """)
    Optional<MovieModel> findMovieByIdWithCategories(@Param("id") Long id);
}
