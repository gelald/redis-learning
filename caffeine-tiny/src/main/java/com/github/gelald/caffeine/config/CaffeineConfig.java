package com.github.gelald.caffeine.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * @author gelald
 * <p>
 * Caffeine的配置类，用于定义Caffeine核心缓存对象
 */
@Configuration
public class CaffeineConfig {

    /**
     * 通过建造者模式构建Caffeine核心缓存对象，后续的读、写操作都基于这个对象进行
     * <p>
     * 这里定义时能指定缓存的各项配置，空间、过期时间等
     * <p>
     * <p>
     * initialCapacity: 初始的缓存空间大小。
     * <p>
     * maximumSize: 缓存的最大数量。/ maximumWeight: 缓存的最大权重。 两者不能同时使用
     * <p>
     * expireAfterAccess: 最后一次读或写操作后经过指定时间过期。/ expireAfterWrite: 最后一次写操作后经过指定时间过期。如果同时指定以 expireAfterWrite 为准
     * <p>
     * refreshAfterWrite: 创建缓存或者最近一次更新缓存后经过指定时间间隔，刷新缓存。
     * <p>
     * weakKeys: 打开key的弱引用。弱引用的对象拥有更短暂的生命周期。在垃圾回收器线程扫描它所管辖的内存区域的过程中，一旦发现了只具有弱引用的对象，不管当前内存空间足够与否，都会回收它的内存。
     * <p>
     * weakValues：打开value的弱引用。/ softValues：打开value的软引用。软引用： 如果一个对象只具有软引用，则内存空间足够，垃圾回收器就不会回收它；如果内存空间不足了，就会回收这些对象的内存。两者不能同时使用
     * <p>
     * recordStats：开发统计功能。
     *
     * @return Caffeine核心缓存对象
     */
    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                // 初始的缓存空间大小
                .initialCapacity(70)
                // 缓存的最大条数
                .maximumSize(200)
                // 设置最后一次写入或访问后经过固定时间过期
                .expireAfterWrite(60, TimeUnit.SECONDS)
                .build();
    }
}
