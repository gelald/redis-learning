package com.github.gelald.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * RedissonClient配置，这个配置只有在implement-type中选择redisson_lock才生效
 *
 * @author ngwingbun
 * date: 2024/7/20
 */
@Configuration
@ConditionalOnProperty(prefix = "anti-shake", name = "implement-type", havingValue = "redisson_lock")
public class RedissonConfiguration {
    private final Environment environment;

    public RedissonConfiguration(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public RedissonClient redissonClient() {
        String address = String.format("redis://%s:%s",
                environment.getProperty("spring.data.redis.host"), environment.getProperty("spring.data.redis.port"));
        String redisPass = environment.getProperty("spring.data.redis.password");
        Integer database = environment.getProperty("spring.data.redis.database", Integer.class, 0);
        // 这里假设使用单节点的Redis服务器
        Config config = new Config();
        config.useSingleServer()
                // 使用与Spring Data Redis相同的地址
                .setAddress(address)
                // 如果有密码
                .setPassword(redisPass)
                // redis的数据库索引
                .setDatabase(database)
                // 连接池大小
                .setConnectionPoolSize(2)
                // 最小空闲连接数
                .setConnectionMinimumIdleSize(1);
        // 创建RedissonClient实例
        return Redisson.create(config);
    }
}
