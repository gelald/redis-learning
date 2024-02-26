package com.github.gelald.distribted.lock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @author Edwin
 */
@EnableAspectJAutoProxy
@SpringBootApplication
public class RedisDistributedLockApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedisDistributedLockApplication.class, args);
    }
}
