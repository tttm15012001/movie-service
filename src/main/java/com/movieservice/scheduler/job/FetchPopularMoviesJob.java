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

    @Value("${config.crawl.current-year}")
    private Integer currentYear;

    @Value("${config.crawl.page-limit}")
    private int pageLimit;

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

        CompletableFuture<?>[] futures = new CompletableFuture[pageLimit];

        for (int page = 1; page <= pageLimit; page++) {
            futures[page - 1] =
                    fetchPopularMoviesAsync(page)
                            .thenApply(this::toMovieModelList)
                            .thenAccept(this::saveMovies);
        }

        CompletableFuture.allOf(futures)
                .exceptionally(err -> {
                    log.error("[{}] Error fetching TMDB pages",
                            context.getJobDetail().getKey().getName(), err);
                    return null;
                })
                .thenRun(() -> log.info("Completed fetching {} TMDB pages", pageLimit));

        return new Result(0, 0, 0, 0);
    }

    private CompletableFuture<List<JsonNode>> fetchPopularMoviesAsync(int page) {
        String url = baseUrl + "/trending/tv/week?language=en-US&page=" + page;

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

        LocalDate releaseDate = LocalDate.parse(movieNode.get("first_air_date").asText());
        if (releaseDate.getYear() != currentYear) return null;

        return MovieModel.builder()
                .tmdbId(tmdbId)
                .searchTitle(originalName)
                .releaseYear(releaseDate.getYear())
                .fetchTime(0)
                .build();
    }

    private void saveMovies(List<MovieModel> movies) {
        for (MovieModel movie : movies) {
            try {
                movieRepository.save(movie);
            } catch (Exception ex) {
                if (ex.getMessage().contains("tmdb_id")) {
                    log.warn("Duplicate movie ignored: tmdbId={}", movie.getTmdbId());
                } else {
                    log.error("Error saving movie {}", movie.getTmdbId(), ex);
                }
            }
        }
    }
}
