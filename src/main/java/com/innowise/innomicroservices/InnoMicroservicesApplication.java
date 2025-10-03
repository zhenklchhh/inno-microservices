package com.innowise.innomicroservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class InnoMicroservicesApplication {
    public static void main(String[] args) {
        SpringApplication.run(InnoMicroservicesApplication.class, args);
    }
}
