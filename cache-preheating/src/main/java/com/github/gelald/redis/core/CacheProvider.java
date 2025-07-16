package com.github.gelald.redis.core;

/**
 * 缓存接口，定义了缓存的行为，包括获取、预热
 * 应用中的缓存数据应该实现这个接口
 */
public interface CacheProvider {

    /**
     * 缓存预热
     */
    void init();

    /**
     * 获取缓存数据
     *
     * @return 缓存值
     */
    <T> T get();

    /**
     * 清除缓存
     */
    void clear();

    /**
     * 重新加载缓存值，先清除再加载
     */
    default void reload() {
        clear();
        init();
    }
}
