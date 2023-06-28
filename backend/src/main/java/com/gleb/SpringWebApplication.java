package com.gleb;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableR2dbcRepositories
    public class SpringWebApplication {

        public static void main(String[] args) {
            SpringApplication.run(com.gleb.SpringWebApplication.class, args);
        }

    }

