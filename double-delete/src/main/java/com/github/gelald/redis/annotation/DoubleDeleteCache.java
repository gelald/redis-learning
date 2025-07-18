package com.github.gelald.redis.annotation;

import java.lang.annotation.*;

/**
 * 延迟双删注解，被这个注解修饰的方法，执行完成后需要对缓存值执行延迟双删
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
public @interface DoubleDeleteCache {
    String name() default "";
}
