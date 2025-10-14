package com.ryanmovie.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ryanmovie.dto.response.MovieResponseDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.JoinTable;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ElementCollection(targetClass = Genre.class)
    @Enumerated(EnumType.STRING)
    @Column(name = "genre")
    protected List<Genre> genre;

    @Column(name = "country")
    protected String country;

    @Column(name = "director")
    protected String director;

    @ManyToMany
    @JoinTable(
            name = "movie_cast_mapping",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "cast_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnore
    private Set<CastModel> cast = new HashSet<>();

    @Column(name = "episodes")
    protected String episodes;

    @Column(name = "status")
    protected String status;

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
                .country(this.getCountry())
                .director(this.getDirector())
                .cast(this.cast != null
                        ? this.cast.stream()
                            .map(CastModel::getName)
                            .toList()
                        : List.of())
                .episodes(this.getEpisodes())
                .status(this.getStatus())
                .description(this.getDescription())
                .releaseDate(this.getReleaseDate())
                .build();
    }

}
