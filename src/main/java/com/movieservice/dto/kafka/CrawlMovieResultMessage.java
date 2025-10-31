package com.movieservice.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CrawlMovieResultMessage {

    private Long movieId;

    private Long metadataId;

    private Integer numberOfEpisodes;

    private Double voteAverage;

}
