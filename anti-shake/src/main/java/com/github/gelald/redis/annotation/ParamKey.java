package com.github.gelald.redis.annotation;

import java.lang.annotation.*;

/**
 * 用于标注哪些参数是作为本次请求key的，作为key的参数不能重复
 *
 * @author ngwingbun
 * date: 2024/7/20
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ParamKey {
}