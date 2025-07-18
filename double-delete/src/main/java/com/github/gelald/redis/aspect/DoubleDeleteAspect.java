package com.github.gelald.redis.aspect;

import com.github.gelald.redis.annotation.DoubleDeleteCache;
import com.github.gelald.redis.dispatcher.DelayDeleteDispatcher;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Aspect
@Component
public class DoubleDeleteAspect {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private DelayDeleteDispatcher delayDeleteDispatcher;

    @Pointcut("@annotation(com.github.gelald.redis.annotation.DoubleDeleteCache)")
    public void pointCut() {

    }

    @Around("pointCut()")
    public Object aroundAdvice(ProceedingJoinPoint proceedingJoinPoint) {
        Signature signature = proceedingJoinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        DoubleDeleteCache annotation = targetMethod.getAnnotation(DoubleDeleteCache.class);
        if (annotation == null) {
            log.info("没有被DoubleDeleteCache注解修饰，无需处理");
            try {
                return proceedingJoinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        }

        assert annotation != null;
        log.info("需要执行延迟双删动作, 目标方法名: {}", signature.getName());
        // 获取自定义注解的name，拼装缓存键
        String key = String.format("%s*", annotation.name());
        redisTemplate.delete(key);

        // 执行controller中的方法业务
        Object proceed = null;
        try {
            proceed = proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        // 交给调度器执行第二次的延迟删除
        delayDeleteDispatcher.dispatcher(key);
        return proceed;
    }
}
