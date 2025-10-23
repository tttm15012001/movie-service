package com.movieservice.messaging.consumer;

import com.movieservice.dto.kafka.CrawlMovieResultMessage;
import com.movieservice.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.movieservice.common.constant.KafkaTopicConstants.TOPIC_MOVIE_CRAWL_RESULT;

@Component
@Slf4j
public class CrawlMovieResultConsumer {

    private final MovieService movieService;

    public CrawlMovieResultConsumer(
            MovieService movieService
    ) {
        this.movieService = movieService;
    }

    @KafkaListener(topics = TOPIC_MOVIE_CRAWL_RESULT, groupId = "movie-service-group")
    public void handleCrawlResult(CrawlMovieResultMessage message) {
        log.info("Received CrawlMovieResultMessage: {}", message);
        this.movieService.getMovieById(message.getMovieId()).ifPresent(movie -> {
            movie.setMetadataId(message.getMetadataId());
            movie.setVoteAverage(message.getVoteAverage());
            movie.setNumberOfEpisodes(message.getNumberOfEpisodes());
            this.movieService.saveMovie(movie);
        });
    }
}
