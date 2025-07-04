package com.github.gelald.redis.properites;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author ngwingbun
 * date: 2024/7/20
 */
@Data
@Component
@ConfigurationProperties(prefix = "anti-shake")
public class AntiShakeProperties {
    private ImplementType implementType;

    public enum ImplementType {
        /**
         * 基于原生的redis的命令
         */
        NATIVE_REDIS,
        /**
         * 基于redisson的分布式锁
         */
        REDISSON_LOCK
    }
}
