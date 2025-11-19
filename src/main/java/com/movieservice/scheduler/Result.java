package com.movieservice.scheduler;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public class Result {
    private final int total;

    private final int success;

    private final int fail;

    private final int skip;
}
