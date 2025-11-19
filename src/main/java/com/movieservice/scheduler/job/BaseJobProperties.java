package com.movieservice.scheduler.job;

import lombok.Data;

import java.time.ZoneId;

@Data
public class BaseJobProperties {
    private String name;
    private String group;
    private String description;
    private String cronExpression;
    private ZoneId timeZone = ZoneId.systemDefault();
}
