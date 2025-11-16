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
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;

@Slf4j
@Component
public class FetchPopularMoviesJob extends BaseJob {

    @Value("${config.tmdb.base-url}")
    private String baseUrl;

    @Value("${config.tmdb.token}")
    private String tmdbToken;

    private final RestTemplate restTemplate;

    private final MovieRepository movieRepository;

    private final ObjectMapper objectMapper;

    @Autowired
    public FetchPopularMoviesJob(RestTemplate restTemplate, MovieRepository movieRepository, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.movieRepository = movieRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    public Result executeJob(JobExecutionContext context) {
        log.info("==== FetchPopularMoviesJob START ====");

        try {
            List<JsonNode> items = fetchPopularMovies();
            List<MovieModel> newMovies = convertNewMovies(items);
            saveMovies(newMovies);

            log.info("==== Saved {} new movies ====", newMovies.size());

        } catch (Exception e) {
            log.error("FetchPopularMoviesJob failed", e);
        }

        return new Result(0, 0, 0, 0);
    }

    private List<JsonNode> fetchPopularMovies() throws IOException {
        String url = baseUrl + "/tv/popular?language=en-US&page=1";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + tmdbToken);
        headers.set("Accept", "application/json");

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IOException("TMDb TV popular fetch failed: " + response.getStatusCode());
        }

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode results = root.get("results");

        if (results == null || !results.isArray()) return List.of();

        return StreamSupport.stream(results.spliterator(), false).toList();
    }

    private List<MovieModel> convertNewMovies(List<JsonNode> items) {
        return items.stream()
                .map(this::toMovieIfNew)
                .filter(Objects::nonNull)
                .toList();
    }

    private MovieModel toMovieIfNew(JsonNode node) {
        long metadataId = node.get("id").asLong();
        String originalName = node.get("original_name").asText();

        // Skip if existed
        if (movieRepository.existsBySearchTitleIgnoreCase(originalName.toLowerCase())) return null;
        if (movieRepository.existsByMetadataId(metadataId)) return null;

        // Extract release year from first_air_date
        LocalDate airDate = LocalDate.parse(node.get("first_air_date").asText());
        int releaseYear = airDate.getYear();

        MovieModel movie = new MovieModel();
        movie.setSearchTitle(originalName);
//        movie.setMetadataId(metadataId);
        movie.setReleaseYear(releaseYear);

        return movie;
    }

    private void saveMovies(List<MovieModel> movies) {
        if (!movies.isEmpty()) {
            movieRepository.saveAll(movies);
        }
    }
}
