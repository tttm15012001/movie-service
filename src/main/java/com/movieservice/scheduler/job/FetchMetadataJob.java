package com.movieservice.scheduler.job;

import com.movieservice.model.entity.MovieModel;
import com.movieservice.repository.MovieRepository;
import com.movieservice.scheduler.Result;
import com.movieservice.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;

import java.util.List;

@Slf4j
public class FetchMetadataJob extends BaseJob {

    private final MovieRepository movieRepository;

    private final MovieService movieService;

    public FetchMetadataJob(
            MovieRepository movieRepository,
            MovieService movieService
    ) {
        this.movieRepository = movieRepository;
        this.movieService = movieService;
    }

    @Override
    public Result executeJob(JobExecutionContext context) {
        List<MovieModel> moviesWithoutMetadata = movieRepository.findAllByMetadataIdIsNull();
        int success = 0;
        int fail = 0;

        for (MovieModel movie : moviesWithoutMetadata) {
            try {
                movieService.fetchMetadata(movie);
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
