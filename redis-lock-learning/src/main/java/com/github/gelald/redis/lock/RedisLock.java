package com.github.gelald.redis.lock;

/**
 * @author WuYingBin
 * date: 2023/6/19
 */
public interface RedisLock {
    void getData(Long dataId) throws Exception;
}

//https://blog.csdn.net/weixin_45150104/article/details/125131846