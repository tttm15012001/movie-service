package com.movieservice.messaging.producer;

import com.movieservice.dto.request.CrawlMovieRequest;
import com.movieservice.model.entity.MovieModel;
import com.movieservice.repository.MovieRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.movieservice.common.constant.KafkaTopicConstants.TOPIC_MOVIE_CRAWL_REQUEST;

@Component
@Slf4j
public class CrawlMovieRequestProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    private final MovieRepository movieRepository;

    @Value("${config.crawl.current-year}")
    private Integer currentYear;

    public CrawlMovieRequestProducer(
        KafkaTemplate<String, Object> kafkaTemplate,
        MovieRepository movieRepository
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.movieRepository = movieRepository;
    }

    public void sendCrawlRequest(List<CrawlMovieRequest> crawlMovieRequests) {
        Set<String> existingSearchTitles = movieRepository.findAllSearchTitle();
        Set<String> existingSearchTitlesWithoutMetadata = movieRepository.findAllSearchTitlesHasNoMetadata();

        crawlMovieRequests.forEach(request -> {
            String searchTitle = request.getTitle();
            Integer releaseYear = request.getReleaseYear() != null ? request.getReleaseYear() : currentYear;

            boolean isNew = !existingSearchTitles.contains(searchTitle.toLowerCase());
            boolean needsMetadata = existingSearchTitlesWithoutMetadata.contains(searchTitle.toLowerCase());

            if (isNew || needsMetadata) {
                MovieModel movie = isNew
                        ? this.saveNewMovie(searchTitle, releaseYear)
                        : movieRepository.findBySearchTitleIgnoreCase(searchTitle).orElseThrow();

                Map<String, Object> payload = Map.of(
                        "movieId", movie.getId(),
                        "title", searchTitle,
                        "originalLanguage", request.getOriginalLanguage(),
                        "releaseYear", releaseYear,
                        "requestedAt", Instant.now().toString()
                );

                kafkaTemplate.send(TOPIC_MOVIE_CRAWL_REQUEST, searchTitle, payload);
                log.info("[{}] - Sent crawl request (movieId = {})", request.getTitle(), movie.getId());
            } else {
                log.info("[{}] - Already have metadata", request.getTitle());
            }
        });
    }


    private MovieModel saveNewMovie(String searchTitle, Integer releaseYear) {
        MovieModel movie = MovieModel.builder()
                .searchTitle(searchTitle)
                .releaseYear(releaseYear)
                .build();

        return this.movieRepository.save(movie);
    }
}
