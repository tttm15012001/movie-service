package com.movieservice.service;

public interface S3Service {
    String generatePresignedCookies(String key, int expireInMinutes);
}
