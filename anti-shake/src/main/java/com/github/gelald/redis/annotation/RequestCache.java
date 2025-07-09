package com.github.gelald.redis.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 定义请求锁，防止客户端重复提交
 *
 * @author ngwingbun
 * date: 2024/7/20
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RequestCache {
    /**
     * redis锁前缀
     *
     * @return 默认为空，但不可为空
     */
    String prefix() default "";

    /**
     * redis锁过期时间
     *
     * @return 默认1500毫秒
     */
    int expire() default 1500;

    /**
     * redis锁过期时间单位
     *
     * @return 默认单位为毫秒
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * redis  key分隔符
     *
     * @return 分隔符
     */
    String delimiter() default "&";
}