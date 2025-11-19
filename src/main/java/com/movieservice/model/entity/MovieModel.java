package com.movieservice.model.entity;

import com.movieservice.dto.response.MovieResponseDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    @Column(unique = true, name = "tmdb_id")
    protected Integer tmdbId;

    @Column(name = "search_title", unique = true)
    private String searchTitle;

    @Column(name = "title")
    private String title;

    @Column(name = "backdrop")
    private String backdrop;

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

    @Column(name = "primary_category_id")
    private Long primaryCategoryId;

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    protected LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    @LastModifiedDate
    protected LocalDateTime lastModifiedDate;

    @Column(name = "fetch_time")
    protected Integer fetchTime;

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
                .title(this.getTitle())
                .backdrop(this.getBackdrop())
                .releaseYear(this.getReleaseYear())
                .voteAverage(this.getVoteAverage())
                .metadataId(this.getMetadataId())
                .primaryCategory(this.getPrimaryCategoryId())
                .categories(
                    this.categories != null
                        ? this.categories.stream()
                        .map(c -> Map.of(c.getId(), c.getName()))
                        .toList()
                        : List.of())
                .build();
    }

}
