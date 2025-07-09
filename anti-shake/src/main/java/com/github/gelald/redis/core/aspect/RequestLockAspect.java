package com.github.gelald.redis.core.aspect;

import cn.hutool.core.text.CharSequenceUtil;
import com.github.gelald.redis.annotation.RequestCache;
import com.github.gelald.redis.core.generator.RequestKeyGenerator;
import com.github.gelald.redis.core.handler.NativeRedisHandler;
import com.github.gelald.redis.core.handler.RedissonLockHandler;
import com.github.gelald.redis.exception.BusinessException;
import com.github.gelald.redis.properites.AntiShakeProperties;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 业务请求切面，在切面中处理业务请求防抖
 *
 * @author ngwingbun
 * date: 2024/7/20
 */
@Slf4j
@Aspect
@Component
public class RequestLockAspect {
    @Autowired
    private AntiShakeProperties antiShakeProperties;
    @Autowired
    private NativeRedisHandler nativeRedisHandler;
    @Autowired
    private RedissonLockHandler redissonLockHandler;

    @Around("execution(public * * (..)) && @annotation(com.github.gelald.redis.annotation.RequestCache)")
    public Object interceptor(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        RequestCache requestLock = method.getAnnotation(RequestCache.class);
        if (requestLock == null) {
            // 没有标记@RequestCache注解的方法直接放行
            return joinPoint.proceed();
        }
        if (CharSequenceUtil.isEmpty(requestLock.prefix())) {
            throw new BusinessException(2, "重复提交前缀不能为空");
        }
        //获取自定义key
        final String lockKey = RequestKeyGenerator.generate(joinPoint, requestLock);
        if (this.antiShakeProperties.getImplementType().equals(AntiShakeProperties.ImplementType.REDISSON_LOCK)) {
            return this.redissonLockHandler.process(joinPoint, lockKey, requestLock);
        } else {
            return this.nativeRedisHandler.process(joinPoint, lockKey, requestLock);
        }
    }
}
