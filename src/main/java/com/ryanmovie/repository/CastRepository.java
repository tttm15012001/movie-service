package com.ryanmovie.repository;

import com.ryanmovie.model.entity.CastModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CastRepository extends JpaRepository<CastModel, Long> {
}
