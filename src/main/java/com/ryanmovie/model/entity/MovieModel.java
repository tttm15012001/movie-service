package com.ryanmovie.model.entity;

import com.ryanmovie.dto.response.MovieResponseDto;
import com.ryanmovie.utils.StringUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import static com.ryanmovie.common.constant.DatabaseConstants.TABLE_MOVIE;

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

    @Column(name = "created_date", nullable = false, updatable = false)
    @CreatedDate
    protected LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    @LastModifiedDate
    protected LocalDateTime lastModifiedDate;

    @Column(name = "title")
    protected String title;

    @Column(name = "original_title")
    protected String originalTitle;

    @Column(name = "description")
    protected String description;

    @Column(name = "manufacturing_date")
    protected Date manufacturingDate;

    @Column(name = "rate_score")
    protected Float rateScore;

    @Column(name = "review_quantity")
    protected Integer reviewQuantity;

    @Column(name = "duration")
    protected Integer duration;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "movie_genre_mapping",
            joinColumns = @JoinColumn(name = "movie_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    protected List<Genre> genre;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "movie_category_mapping",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private List<CategoryModel> categories;

    @Column(name = "country")
    protected String country;

    @Column(name = "director")
    protected String director;

    @Column(name = "casts")
    private String casts;

    @Column(name = "episodes")
    protected Integer episodes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    protected MovieStatus status;

    @Column(name = "release_date")
    protected Date releaseDate;

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
                .originalTitle(this.getOriginalTitle())
                .manufacturingDate(this.getManufacturingDate())
                .rateScore(this.getRateScore())
                .reviewQuantity(this.getReviewQuantity())
                .duration(this.getDuration())
                .genre(this.genre != null
                        ? this.genre.stream()
                            .map(Genre::getVietnameseName)
                            .toList()
                        : List.of())
                .categories(this.categories != null
                        ? this.categories.stream()
                            .map(CategoryModel::getName)
                            .toList()
                        : List.of())
                .country(this.getCountry())
                .director(this.getDirector())
                .casts(StringUtil.convertStringToList(this.casts))
                .episodes(this.getEpisodes())
                .status(this.getStatus())
                .description(this.getDescription())
                .releaseDate(this.getReleaseDate())
                .build();
    }

}
