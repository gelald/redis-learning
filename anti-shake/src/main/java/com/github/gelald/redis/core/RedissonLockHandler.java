package com.github.gelald.redis.core;

import com.github.gelald.redis.annotation.RequestCache;
import com.github.gelald.redis.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author ngwingbun
 * date: 2024/7/20
 */
@Slf4j
@Component
public class RedissonLockHandler {
    @Autowired(required = false)
    private RedissonClient redissonClient;

    public Object process(ProceedingJoinPoint joinPoint, String lockKey, RequestCache requestLock) {
        // 使用Redisson分布式锁的方式判断是否重复提交
        RLock lock = redissonClient.getLock(lockKey);
        boolean isLocked = false;
        try {
            //尝试抢占锁
            isLocked = lock.tryLock();
            //没有拿到锁说明已经有了请求了
            if (!isLocked) {
                throw new BusinessException(1, "您的操作太快了,请稍后重试");
            }
            //拿到锁后设置过期时间
            lock.lock(requestLock.expire(), requestLock.timeUnit());
            try {
                return joinPoint.proceed();
            } catch (Throwable throwable) {
                throw new BusinessException(2, "系统异常");
            }
        } catch (Exception e) {
            throw new BusinessException(1, "您的操作太快了,请稍后重试");
        } finally {
            //释放锁
            if (isLocked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
}
