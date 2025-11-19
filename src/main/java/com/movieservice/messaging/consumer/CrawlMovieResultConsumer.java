package com.movieservice.messaging.consumer;

import com.movieservice.dto.kafka.CrawlMovieResultMessage;
import com.movieservice.model.entity.CategoryModel;
import com.movieservice.service.CategoryService;
import com.movieservice.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.movieservice.common.constant.KafkaTopicConstants.TOPIC_MOVIE_CRAWL_RESULT;

@Component
@Slf4j
public class CrawlMovieResultConsumer {

    private final MovieService movieService;

    private final CategoryService categoryService;

    public CrawlMovieResultConsumer(
            MovieService movieService,
            CategoryService categoryService
    ) {
        this.movieService = movieService;
        this.categoryService = categoryService;
    }

    @KafkaListener(topics = TOPIC_MOVIE_CRAWL_RESULT, groupId = "movie-service-group")
    public void handleCrawlResult(CrawlMovieResultMessage message) {
        log.info("Received CrawlMovieResultMessage: {}", message);
        this.movieService.getMovieById(message.getMovieId()).ifPresent(movie -> {
            List<CategoryModel> categoryModels = this.categoryService.handleCategory(message.getGenres());
            movie.setMetadataId(message.getMetadataId());
            movie.setTitle(message.getTitle());
            movie.setVoteAverage(message.getVoteAverage());
            movie.setBackdrop(message.getBackdrop());
            movie.setCategories(categoryModels);
            if (categoryModels.get(0) != null) {
                movie.setPrimaryCategoryId(categoryModels.get(0).getId());
            }

            this.movieService.saveMovie(movie);
        });
    }
}
