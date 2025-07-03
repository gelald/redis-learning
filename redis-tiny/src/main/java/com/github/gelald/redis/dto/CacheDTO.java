package com.github.gelald.redis.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CacheDTO {
    @NotNull(message = "缓存对象的key不能为null")
    private String key;
    @NotNull(message = "缓存对象的value不能为null")
    private Object value;
}
