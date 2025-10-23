package com.movieservice.model.entity;

import com.movieservice.dto.response.MovieResponseDto;
import com.movieservice.utils.StringUtil;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.EnumType;
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
import java.util.Date;
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

    @Column(name = "tmdb_id")
    protected Integer tmdbId;

    @Column(name = "for_adult")
    protected Boolean forAdult;

    @Column(name = "title")
    protected String title;

    @Column(name = "original_title")
    protected String originalTitle;

    @Column(name = "description", length = 2000)
    protected String description;

    @Column(name = "number_of_episodes")
    protected Integer numberOfEpisodes;

    @Column(name = "vote_average")
    protected Double voteAverage;

    @Column(name = "vote_count")
    protected Integer voteCount;

    @Column(name = "popularity")
    protected Double popularity;

    @Column(name = "poster_path")
    protected String posterPath;

    @Column(name = "backdrop_path")
    protected String backdropPath;

    @Column(name = "release_date")
    protected Date releaseDate;

    @Column(name = "country")
    protected String country;

    @Column(name = "original_language")
    protected String originalLanguage;

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

    @Column(name = "director")
    protected String director;

    @Column(name = "casts")
    private String casts;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    protected MovieStatus status;

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
                .tmdbId(this.getTmdbId())
                .forAdult(this.getForAdult())
                .title(this.getTitle())
                .originalTitle(this.getOriginalTitle())
                .description(this.getDescription())
                .numberOfEpisodes(this.getNumberOfEpisodes())
                .voteAverage(this.getVoteAverage())
                .voteCount(this.getVoteCount())
                .popularity(this.getPopularity())
                .posterPath(this.getPosterPath())
                .backdropPath(this.getBackdropPath())
                .releaseDate(this.getReleaseDate())
                .country(this.getCountry())
                .originalLanguage(this.getOriginalLanguage())
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
                .director(this.getDirector())
                .casts(StringUtil.convertStringToList(this.casts))
                .status(this.getStatus())
                .build();
    }

}
