package com.gleb;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication (scanBasePackages = "com.gleb")
    public class SpringWebApplication {

        public static void main(String[] args) {
            SpringApplication.run(com.gleb.SpringWebApplication.class, args);
        }

    }

