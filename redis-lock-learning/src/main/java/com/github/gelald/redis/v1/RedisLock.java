package com.github.gelald.redis.v1;

import cn.hutool.core.lang.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author WuYingBin
 * Date 2023/6/15
 */
@Slf4j
@Deprecated
@Component("RedisLock1")
public class RedisLock {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    //锁的前缀名称
    private static final String LOCK_PREFIX = "GET_LOCK";
    //锁的过期时间，尽可能避免业务没有执行完锁就自动释放
    private static final long EXPIRE_TIME = 300L;

    public void lock(String lockName) {
        String key = LOCK_PREFIX + lockName;
        //加锁
        Boolean lockSuccessfully = redisTemplate.opsForValue().setIfAbsent(key, null);
        if (Boolean.TRUE.equals(lockSuccessfully)) {
            try {
                log.info(" ************ Redis加锁成功：{} ************ ", key);
                //设置过期时间，防止出现死锁，程序崩溃、服务器宕机都是不会释放锁的
                redisTemplate.expire(key, EXPIRE_TIME, TimeUnit.SECONDS);
                //处理业务逻辑
                log.info("处理业务中");
                TimeUnit.SECONDS.sleep(5);
            } catch (Exception e) {
                log.info("业务处理过程中出现异常：{}", e.getMessage());
            } finally {
                //加锁处理的逻辑完成，手动释放锁
                redisTemplate.delete(key);
                log.info(" ************ Redis释放锁成功：{} ************ ", key);
            }
        } else {
            log.info("获取锁失败");
        }
    }
}


//https://blog.csdn.net/weixin_45807440/article/details/126122646