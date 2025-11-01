package com.movieservice.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MovieResponseDto {

    private Long id;

    private String searchTitle;

    private Integer releaseYear;

    private Double voteAverage;

    private Long metadataId;

    private Integer numberOfEpisodes;

    private List<String> categories;

    private MetadataResponseDto metadata;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MetadataResponseDto {

        private Long id;

        private Long movieId;

        private Integer tmdbId;

        private Boolean forAdult;

        private String title;

        private String originalTitle;

        private String description;

        private Integer numberOfEpisodes;

        private Double voteAverage;

        private Integer voteCount;

        private Double popularity;

        private String posterPath;

        private String backdropPath;

        @JsonFormat(pattern = "dd-MM-yyyy")
        private LocalDate releaseDate;

        private String country;

        private String originalLanguage;

        private List<String> genre;

        private List<ActorResponseDto> actors;

        private String status;

        @Data
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class ActorResponseDto {

            private Long id;

            private String name;

            private String profilePath;

            private String characterName;

        }

    }
}
