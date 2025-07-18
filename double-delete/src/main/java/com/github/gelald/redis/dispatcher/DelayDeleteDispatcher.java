package com.github.gelald.redis.dispatcher;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.NamedThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 延迟删除的调度器，实际应用中应该使用分布式任务调度框架，如xxl-job等
 */
@Slf4j
@Component
public class DelayDeleteDispatcher {
    private final ThreadPoolExecutor delayDeleteExecutor;
    private final Integer delay;
    private final RedisTemplate<String, Object> redisTemplate;

    public DelayDeleteDispatcher(RedisTemplate<String, Object> redisTemplate) {
        // 可以考虑使用配置文件来决定线程池的属性
        delayDeleteExecutor = ExecutorBuilder.create()
                .setCorePoolSize(2)
                .setMaxPoolSize(2)
                .setThreadFactory(new NamedThreadFactory("delay-delete-cache-", true)).build();
        delay = 1500;
        this.redisTemplate = redisTemplate;
    }

    public void dispatcher(String key) {
        // 开一条线程来实现延迟删除这个key
        CompletableFuture.runAsync(() -> {
            // 异常发生在runAsync/supplyAsync内部，需要用try-catch处理
            try {
                // 延迟1.5秒后删除
                TimeUnit.MILLISECONDS.sleep(delay);
                Boolean deleteResult = redisTemplate.delete(key);
                log.info("after delay {} ms, delete the key [{}] again, result: {}",
                        delay, key, deleteResult);
            } catch (InterruptedException e) {
                log.error("interrupt exception", e);
            }
        }, delayDeleteExecutor);
    }
}
