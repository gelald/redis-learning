package com.github.gelald.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * @author ngwingbun
 * date: 2024/7/20
 */
@Configuration
public class RedissonConfiguration {
//    @Value("${redis_host}")
//    private String redisHost;
    
    @Autowired
    private Environment environment;

    @Bean
    @ConditionalOnProperty(prefix = "anti-shake", name = "implement-type", havingValue = "redisson_lock")
    public RedissonClient redissonClient() {
        Config config = new Config();
        String address = String.format("redis://%s:%s",
                environment.getProperty("redis_host"), environment.getProperty("redis_port"));
        String redisPass = environment.getProperty("redis_pass");
        // 这里假设使用单节点的Redis服务器
        config.useSingleServer()
                // 使用与Spring Data Redis相同的地址
                .setAddress(address)
                // 如果有密码
                .setPassword(redisPass)
                // 其他配置参数
                .setDatabase(0);
        //.setConnectionPoolSize(10)
        //.setConnectionMinimumIdleSize(2);
        // 创建RedissonClient实例
        return Redisson.create(config);
    }
}
