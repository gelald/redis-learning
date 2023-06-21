package com.github.gelald.redis.lock;

import cn.hutool.core.util.StrUtil;
import com.github.gelald.redis.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 代码思路：为了解决加锁后，程序崩溃等问题导致锁无法被释放、其他需要拿锁的线程一直等待的问题，
 * 在获取Redis锁的时候加上过期时间，到期自动释放锁，即使程序崩溃也可以让其他线程拿到锁
 * </p>
 *
 * <p>
 * 存在问题：释放的锁可能已经是已到期释放，其他线程获取的相同的锁了
 * </p>
 *
 * @author WuYingBin
 * date: 2023/6/20
 */
@Slf4j
@Service
public class RedisLockV2 implements RedisLock {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

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
            try {
                Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, "lock", 30, TimeUnit.SECONDS);
                if (Boolean.TRUE.equals(ifAbsent)) {
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
                redisTemplate.delete(lockKey);
            }
        } else {
            log.info("缓存中有数据，直接获取");
            data = "从缓存中查询出来的数据";
        }
        log.info("查询数据：{}", data);
    }
}
