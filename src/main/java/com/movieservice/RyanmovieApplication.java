package com.movieservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class RyanmovieApplication {

    public static void main(String[] args) {
        SpringApplication.run(RyanmovieApplication.class, args);
    }

}
