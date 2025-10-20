package com.movieservice.messaging.consumer;

import com.movieservice.dto.kafka.CrawlMovieResultMessage;
import com.movieservice.model.entity.MovieModel;
import com.movieservice.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

import static com.movieservice.common.constant.KafkaTopicConstants.TOPIC_MOVIE_CRAWL_RESULT;

@Component
@RequiredArgsConstructor
public class CrawlMovieResultConsumer {

    private final MovieRepository movieRepository;

    @KafkaListener(topics = TOPIC_MOVIE_CRAWL_RESULT, groupId = "movie-service")
    public void onMetadataResult(CrawlMovieResultMessage data) {
        String title = data.getTitle();
        System.out.println("[Consumer] Received metadata for: " + title);

        movieRepository.findByTitleIgnoreCase(title).ifPresentOrElse(movie -> {
            System.out.println("[DB - Skipped] movie already exists: " + title);
        }, () -> {
            MovieModel newMovie = createNewMovie(data);
            movieRepository.save(newMovie);
            System.out.println("[DB - Created] new movie: " + title);
        });
    }

    private MovieModel createNewMovie(CrawlMovieResultMessage data) {
        return MovieModel.builder()
                .tmdbId(data.getId())
                .forAdult(data.getForAdult())
                .title(data.getTitle())
                .originalTitle(data.getOriginalTitle())
                .description(data.getDescription())
                .numberOfEpisodes(data.getNumberOfEpisodes())
                .voteAverage(data.getVoteAverage())
                .voteCount(data.getVoteCount())
                .popularity(data.getPopularity())
                .posterPath(data.getPosterPath())
                .backdropPath(data.getBackdropPath())
                .releaseDate(parseDate(data.getReleaseDate()))
                .country(data.getCountry())
                .originalLanguage(data.getOriginalLanguage())
                .build();
    }

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception e) {
            System.err.println("[WARN] Invalid releaseDate format: " + dateStr);
            return null;
        }
    }
}
