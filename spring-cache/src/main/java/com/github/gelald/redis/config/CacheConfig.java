package com.github.gelald.redis.config;

import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@EnableCaching
@Configuration
public class CacheConfig {
    /**
     * 使 Redis 配置(spring.cache.redis)生效
     * 配置 Redis 自定义前缀 (可按需扩展)
     * 也可以通过: config.computePrefixWith(cacheName -> "redisProperties.getKeyPrefix()" + cacheName + ":"); 进行自定义配置, 本意是对函数式接口的实现
     *
     * @param cacheProperties Redis 配置参数
     * @return
     */
    @Bean
    public RedisCacheConfiguration redisCacheConfiguration(CacheProperties cacheProperties) {
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        //设置key用string类型保存，value用json格式保存
        config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        CacheProperties.Redis redisProperties = cacheProperties.getRedis();
        // 使配置文件中所有的配置都生效
        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            // 自定义前缀: spring.cache.redis.key-prefix
            // + 入参(org.springframework.data.redis.cache.CacheKeyPrefix.compute(String cacheName))
            // + value值 @Cacheable(value = "selectPage")
            config = config.computePrefixWith(cacheName -> redisProperties.getKeyPrefix() + cacheName + ":");
            // 前缀格式: spring.cache.redis.key-prefix + value值 @Cacheable(value = "selectPage")
            // config = config.prefixKeysWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            // 写入 Redis 时是否使用前缀: org.springframework.data.redis.cache.RedisCache.createCacheKey
            config = config.disableKeyPrefix();
        }

        return config;
    }
}