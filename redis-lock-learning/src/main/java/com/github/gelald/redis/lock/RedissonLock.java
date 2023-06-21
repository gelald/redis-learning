package com.github.gelald.redis.lock;

import cn.hutool.core.util.StrUtil;
import com.github.gelald.redis.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 代码思路：业务没有执行完，锁就到期释放了，可以使用一个后台线程以固定的时间周期不断查看redis锁是否要过期了，
 * 如果还没过期就要延长锁的过期时间，这个技术也叫看门狗机制，Redisson对此已有实现
 * 另外Redisson也可以轻松解决释放掉其他线程加的锁的这个问题
 * </p>
 *
 * @author WuYingBin
 * date: 2023/6/20
 */
@Slf4j
@Service
public class RedissonLock implements RedisLock {
    @Resource
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private RedissonClient redissonClient;

    @Override
    public void getData(Long dataId) throws Exception {
        //组装缓存key
        String key = RedisConstant.CACHE_KEY_PREFIX + dataId;
        String lockKey = RedisConstant.LOCK_KEY_PREFIX + dataId;
        //尝试从缓存中获取数据
        log.info("尝试从缓存中获取数据.......");
        String data = redisTemplate.opsForValue().get(key);
        //缓存中没有拿到数据，去数据库中拿取数据
        if (StrUtil.isEmpty(data)) {
            log.info("缓存数据为空，尝试获取锁去查询数据库");
            RLock lock = redissonClient.getLock(lockKey);
            try {
                if (lock.tryLock(0, 30, TimeUnit.SECONDS)) {
                    //只有拿到锁的线程才能从数据库中获取数据
                    log.info("成功拿到锁，正在从数据库中获取数据......");
                    TimeUnit.MILLISECONDS.sleep(300);
                    data = "从数据库查询出来的数据";
                    //把数据库中查询出来的数据放入缓存中
                    redisTemplate.opsForValue().set(key, data);
                    log.info("把数据库中查询出来的数据放入缓存中");
                } else {
                    //没有拿到锁的线程请等待
                    TimeUnit.SECONDS.sleep(2);
                    this.getData(dataId);
                }
            } finally {
                //锁用完后释放
                if (lock.isLocked()) {
                    //判断锁是否还是自己的
                    if (lock.isHeldByCurrentThread()) {
                        lock.unlock();
                    }
                }
            }
        } else {
            log.info("缓存中有数据，直接获取");
            data = "从缓存中查询出来的数据";
        }
        log.info("查询数据：{}", data);
    }
}
