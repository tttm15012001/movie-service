package com.ryanmovie.service;

public interface S3Service {
    String generatePresignedCookies(String key, int expireInMinutes);
}
