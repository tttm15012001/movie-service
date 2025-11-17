package com.movieservice.scheduler.job;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieservice.model.entity.MovieModel;
import com.movieservice.repository.MovieRepository;
import com.movieservice.scheduler.Result;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class FetchPopularMoviesJob extends BaseJob {

    @Value("${config.tmdb.base-url}")
    private String baseUrl;

    @Value("${config.tmdb.token}")
    private String tmdbToken;

    private final MovieRepository movieRepository;
    private final ObjectMapper objectMapper;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Autowired
    public FetchPopularMoviesJob(MovieRepository movieRepository,
                                 ObjectMapper objectMapper) {
        this.movieRepository = movieRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Result executeJob(JobExecutionContext context) {
        log.info("[{}] Executing job", context.getJobDetail().getKey().getName());

        fetchPopularMoviesAsync()
                .thenApply(this::toMovieModelList)
                .thenAccept(this::saveMovies)
                .exceptionally(err -> {
                    log.error("[{}] Error while fetching movies", context.getJobDetail().getKey().getName(), err);
                    return null;
                });

        return new Result(0, 0, 0, 0);
    }

    private CompletableFuture<List<JsonNode>> fetchPopularMoviesAsync() {
        String url = baseUrl + "/tv/popular?language=en-US&page=1";

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", "Bearer " + tmdbToken)
                .headers("Accept", "application/json")
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parsePopularMovies);
    }

    private List<JsonNode> parsePopularMovies(String json) {
        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode results = root.get("results");

            if (results == null || !results.isArray()) return List.of();

            return StreamSupport.stream(results.spliterator(), false).toList();

        } catch (Exception e) {
            log.error("Error parsing TMDB JSON", e);
            return List.of();
        }
    }

    private List<MovieModel> toMovieModelList(List<JsonNode> movies) {
        return movies.stream()
                .map(this::toMovieModelIfNew)
                .filter(Objects::nonNull)
                .toList();
    }

    private MovieModel toMovieModelIfNew(JsonNode movieNode) {
        int tmdbId = movieNode.get("id").asInt();
        String originalName = movieNode.get("original_name").asText();

        if (this.movieRepository.existsByTmdbId(tmdbId)) return null;
        if (this.movieRepository.existsBySearchTitleIgnoreCase(originalName)) return null;

        LocalDate releaseDate = LocalDate.parse(movieNode.get("first_air_date").asText());

        return MovieModel.builder()
                .tmdbId(tmdbId)
                .searchTitle(originalName)
                .releaseYear(releaseDate.getYear())
                .fetchTime(0)
                .build();
    }

    private void saveMovies(List<MovieModel> movies) {
        if (movies.isEmpty()) return;

        this.movieRepository.saveAll(movies);
        log.info("[{}] Movies saved", movies.size());
    }
}
