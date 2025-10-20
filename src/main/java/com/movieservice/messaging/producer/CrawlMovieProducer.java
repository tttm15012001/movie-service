package com.movieservice.messaging.producer;

import com.movieservice.dto.request.CrawlMovieRequest;
import com.movieservice.model.entity.MovieModel;
import com.movieservice.repository.MovieRepository;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.movieservice.common.constant.KafkaTopicConstants.TOPIC_MOVIE_CRAWL_REQUEST;

@Component
public class CrawlMovieProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final MovieRepository movieRepository;

    public CrawlMovieProducer(
        KafkaTemplate<String, Object> kafkaTemplate,
        MovieRepository movieRepository
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.movieRepository = movieRepository;
    }

    public void sendCrawlRequest(List<CrawlMovieRequest> crawlMovieRequests) {
        crawlMovieRequests.forEach(request -> {
            String movieTitle = request.getTitle();
            Optional<MovieModel> movieModelOpt = movieRepository.findByTitleIgnoreCase(movieTitle);
            if (movieModelOpt.isEmpty()) {
                Map<String, Object> payload = Map.of(
                        "title", request.getTitle(),
                        "releaseYear", request.getReleaseYear(),
                        "originalLanguage", request.getOriginalLanguage(),
                        "requestedAt", Instant.now().toString()
                );

                kafkaTemplate.send(TOPIC_MOVIE_CRAWL_REQUEST, payload);
                System.out.println("🎯 [Producer] Sent crawl request for: " + request.getTitle());
            }
        });
    }
}
