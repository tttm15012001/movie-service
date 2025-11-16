package com.movieservice.scheduler.job;

import com.movieservice.config.properties.SchedulerProperties;
import com.movieservice.scheduler.Result;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public abstract class BaseJob extends QuartzJobBean {

    private static final int MAX_RETRY = 3;

    @Autowired
    private SchedulerProperties schedulerProperties;

    protected abstract Result executeJob(JobExecutionContext context) throws JobExecutionException;

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        log.info("Executing job {} at {}",
                context.getJobDetail().getKey(),
                context.getTrigger().getPreviousFireTime()
        );
        long startTime = System.currentTimeMillis();
        JobKey jobKey = context.getJobDetail().getKey();
        try {
            Result result = executeJob(context);
            log.info("Job [{}] process complete with result [{}]", jobKey, result);
            if (result.getFail() > 0) {
                checkRetryJob(context, new RuntimeException("Job execute with " + result.getFail() + "failed records"));
            }
        } catch (JobExecutionException e) {
            checkRetryJob(context, e);
            throw e;
        } finally {
            log.info("Finish execution job [{}], execution time [{}] ms", jobKey, System.currentTimeMillis() - startTime);
        }
    }

    private void checkRetryJob(JobExecutionContext context, Exception rootCause) throws JobExecutionException {
        int maxRetry = Optional.ofNullable(schedulerProperties).map(SchedulerProperties::getMaxRetry).orElse(MAX_RETRY);
        if (context.getRefireCount() >= maxRetry) {
            throw new JobExecutionException(String.format("Job failed more than %d times, giving up", maxRetry), rootCause);
        }
        log.warn("Job [{}] failed, attempt to retry #{}", context.getJobDetail().getKey(), context.getRefireCount() + 1);
        throw new JobExecutionException(rootCause, true);
    }
}
