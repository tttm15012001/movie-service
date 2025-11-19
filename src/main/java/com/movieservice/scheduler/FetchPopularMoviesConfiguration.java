package com.movieservice.scheduler;

import com.movieservice.scheduler.job.BaseJobProperties;
import com.movieservice.scheduler.job.FetchPopularMoviesJob;
import org.quartz.JobDetail;
import org.quartz.JobBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.CronScheduleBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.TimeZone;

@Configuration
@ConfigurationProperties(prefix = "quartz.job.fetch-popular-movies")
public class FetchPopularMoviesConfiguration extends BaseJobProperties {

    @Bean
    @Qualifier("fetchPopularMovies")
    public JobDetail fetchPopularMoviesJobDetail() {
        return JobBuilder.newJob().ofType(FetchPopularMoviesJob.class)
                .storeDurably(true)
                .requestRecovery(true)
                .withIdentity(this.getName(), this.getGroup())
                .withDescription(this.getDescription())
                .build();
    }

    @Bean
    public Trigger fetchPopularMoviesTrigger(
            @Qualifier("fetchPopularMovies") JobDetail jobDetail
    ) {
        return TriggerBuilder.newTrigger().withIdentity(this.getName(), this.getGroup())
                .forJob(jobDetail)
                .withSchedule(CronScheduleBuilder.cronSchedule(this.getCronExpression())
                        .inTimeZone(TimeZone.getTimeZone(this.getTimeZone()))
                        .withMisfireHandlingInstructionIgnoreMisfires())
                .startNow()
                .build();
    }

}
