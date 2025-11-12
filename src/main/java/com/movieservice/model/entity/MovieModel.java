package com.movieservice.model.entity;

import com.movieservice.dto.response.MovieResponseDto;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

import static com.movieservice.common.constant.DatabaseConstants.TABLE_MOVIE;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = TABLE_MOVIE)
public class MovieModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @Column(name = "search_title")
    private String searchTitle;

    @Column(name = "release_year")
    protected Integer releaseYear;

    @Column(name = "metadata_id")
    private Long metadataId;

    @Column(name = "vote_average")
    protected Double voteAverage;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_category_mapping",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<CategoryModel> categories;

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    protected LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    @LastModifiedDate
    protected LocalDateTime lastModifiedDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedDate = LocalDateTime.now();
    }

    public MovieResponseDto toMovieResponseDto() {
        return MovieResponseDto.builder()
                .id(this.getId())
                .releaseYear(this.getReleaseYear())
                .voteAverage(this.getVoteAverage())
                .metadataId(this.getMetadataId())
                .categories(this.categories != null
                        ? this.categories.stream()
                            .map(CategoryModel::getName)
                            .toList()
                        : List.of())
                .build();
    }

}
