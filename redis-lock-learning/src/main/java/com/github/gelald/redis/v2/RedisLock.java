package com.github.gelald.redis.v2;

import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author WuYingBin
 * Date 2023/6/15
 */
@Slf4j
@Component
public class RedisLock {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    //锁的前缀名称
    private static final String LOCK_PREFIX = "GET_LOCK";
    //锁的过期时间，尽可能避免业务没有执行完锁就自动释放
    private static final long EXPIRE_TIME = 300L;

    public void lock(String lockName) {
        String key = LOCK_PREFIX + lockName;
        String value = UUID.randomUUID().toString(true);
        try {
            Boolean setFlag = redisTemplate.opsForValue().setIfAbsent(key, value, EXPIRE_TIME, TimeUnit.SECONDS);
            if (Boolean.TRUE.equals(setFlag)) {
                log.info(" ************ Redis加锁成功：{} ************ ", key);
                //设置过期时间，防止出现死锁，程序崩溃、服务器宕机都是不会释放锁的
                redisTemplate.expire(key, EXPIRE_TIME, TimeUnit.SECONDS);
                //处理业务逻辑
                log.info("处理业务中");
                TimeUnit.SECONDS.sleep(5);
            } else {
                log.info("获取锁失败");
            }
        } catch (Exception e) {
            log.info("业务处理过程中出现异常：{}", e.getMessage());
        } finally {
            //为了防止在释放锁时，原有锁已经过期自动释放，而释放的是其他的锁
            if (Objects.equals(redisTemplate.opsForValue().get(key), value)) {
                redisTemplate.delete(key);
            }
        }
    }
}
