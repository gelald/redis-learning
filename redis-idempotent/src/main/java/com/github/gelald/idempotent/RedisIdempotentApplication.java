package com.github.gelald.idempotent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Edwin
 */
@SpringBootApplication
public class RedisIdempotentApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisIdempotentApplication.class, args);
    }

}