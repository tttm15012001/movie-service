package com.ryanmovie.repository;

import com.ryanmovie.model.entity.CategoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryModel, Long> {

    List<CategoryModel> findAllByOrderByScoreDesc(Pageable pageable);
}
