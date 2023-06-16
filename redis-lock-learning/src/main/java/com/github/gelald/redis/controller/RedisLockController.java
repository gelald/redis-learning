package com.github.gelald.redis.controller;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.LongAdder;

/**
 * @author WuYingBin
 * date: 2023/6/16
 */
@Slf4j
@RestController
@RequestMapping("/redis-lock")
public class RedisLockController {
    /**
     * 使用StringRedisTemplate方便用decrement方法
     * 如果使用RedisTemplate，那么需要自定义序列化方法，否则调用失败，提示ERR
     */
    @Autowired
    private StringRedisTemplate redisTemplate;
    /**
     * 加锁
     */
    @Autowired
    private RedissonClient redisson;
    /**
     * 区分不同服务
     */
    @Value("${server.port}")
    private Integer port;

    /**
     * 库存键
     */
    private static final String INVENTORY = "product_inventory";
    /**
     * 库存值
     */
    private static final int INVENTORY_COUNT = 2000;
    /**
     * 计数器，看看是否被调用了对应的次数
     */
    private static final LongAdder LONG_ADDER = new LongAdder();

    /**
     * 初始化库存
     */
    @PostMapping("/inventory-init")
    public void init() {
        redisTemplate.opsForValue().set(INVENTORY, String.valueOf(INVENTORY_COUNT));
    }

    /**
     * 模拟扣库存
     */
    @PostMapping("/inventory-minus")
    public void minus() {
        String lockName = "product_inventory_lock";
        //获取锁
        RLock lock = redisson.getLock(lockName);
        try {
            //加锁
            lock.lock();
            Long decrement = redisTemplate.opsForValue().decrement(INVENTORY);
            LONG_ADDER.increment();
            log.info("服务端口:{}，库存扣减后为:{}，服务调用次数:{}", port, decrement, LONG_ADDER.intValue());
        } catch (Exception e) {
            log.error("发生异常：", e);
        } finally {
            //释放锁
            lock.unlock();
        }
    }
}
