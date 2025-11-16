package com.movieservice.scheduler;

import com.movieservice.scheduler.job.BaseJobProperties;
import com.movieservice.scheduler.job.FetchMetadataJob;
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
@ConfigurationProperties(prefix = "quartz.job.fetch-metadata-job")
public class FetchMetadataJobConfiguration extends BaseJobProperties {

    @Bean
    @Qualifier("fetchMetadataDetail")
    public JobDetail fetchMetadataJobDetail() {
        return JobBuilder.newJob().ofType(FetchMetadataJob.class)
                .storeDurably(true)
                .requestRecovery(true)
                .withIdentity(this.getName(), this.getGroup())
                .withDescription(this.getDescription())
                .build();
    }

    @Bean
    public Trigger notificationSubmitLoanTrigger(
            @Qualifier("fetchMetadataDetail") JobDetail jobDetail
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
