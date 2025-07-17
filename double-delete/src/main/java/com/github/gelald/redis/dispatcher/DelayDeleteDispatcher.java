package com.github.gelald.redis.dispatcher;

import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.thread.NamedThreadFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 延迟删除的调度器，实际应用中应该使用分布式任务调度框架，如xxl-job等
 */
@Component
public class DelayDeleteDispatcher {
    private final ThreadPoolExecutor EXECUTOR;
    private final Integer DELAY;

    public DelayDeleteDispatcher() {
        // 可以考虑使用配置文件来决定线程池的属性
        EXECUTOR = ExecutorBuilder.create()
                .setCorePoolSize(2)
                .setMaxPoolSize(2)
                .setThreadFactory(new NamedThreadFactory("delay-delete-cache-", true)).build();
        DELAY = 1500;
    }

    public void dispatcher(String key) {
        // 延迟删除这个key
    }
}
