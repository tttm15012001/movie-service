package com.movieservice.scheduler.job;

import com.movieservice.model.entity.MovieModel;
import com.movieservice.repository.MovieRepository;
import com.movieservice.scheduler.Result;
import com.movieservice.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class FetchMetadataJob extends BaseJob {

    @Value("${config.metadata.maxRetry}")
    private Integer maxRetry;

    private final MovieRepository movieRepository;

    private final MovieService movieService;

    @Autowired
    public FetchMetadataJob(
            MovieRepository movieRepository,
            MovieService movieService
    ) {
        this.movieRepository = movieRepository;
        this.movieService = movieService;
    }

    @Override
    public Result executeJob(JobExecutionContext context) {
        List<MovieModel> moviesWithoutMetadata = movieRepository.findAllByFetchTimeLessThanAndMetadataIdIsNull(maxRetry);
        int success = 0;
        int fail = 0;

        for (MovieModel movie : moviesWithoutMetadata) {
            try {
                movieService.fetchMetadata(movie);
                movieService.incrementFetchTime(movie.getId());
                success++;
            } catch (Exception ex) {
                log.error("Failed to publish metadata request for movie id={} title={}",
                        movie.getId(), movie.getSearchTitle(), ex);
                fail++;
            }
        }

        return new Result(totalCount(moviesWithoutMetadata), success, fail, 0);
    }

    private int totalCount(List<MovieModel> list) {
        return list == null ? 0 : list.size();
    }

}
