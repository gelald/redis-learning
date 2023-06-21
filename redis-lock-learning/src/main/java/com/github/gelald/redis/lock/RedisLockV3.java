package com.github.gelald.redis.lock;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.github.gelald.redis.constant.RedisConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 代码思路：因为之前加锁时，value都是设置为一个统一的值，
 * 那么等到释放锁时就无法断定这是否是自己加的锁。
 * 解决方案其实很简单：在加锁时设置value为一个随机数，
 * 那么在finally释放锁时就判断一下这个value是不是加锁时的value就能判断是不是自己加的锁了
 * </p>
 *
 * <p>
 * 存在问题：释放掉其他线程的锁本质上的原因还是：
 * 业务没有执行完，加的锁就已经过期释放了，但是从一开始就预估业务执行的最大时间也不现实
 * </p>
 *
 * @author WuYingBin
 * date: 2023/6/20
 */
@Slf4j
@Service
public class RedisLockV3 implements RedisLock {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void getData(Long dataId) throws Exception {
        //组装缓存key
        String key = RedisConstant.CACHE_KEY_PREFIX + dataId;
        String lockKey = RedisConstant.LOCK_KEY_PREFIX + dataId;
        String lockValue = UUID.randomUUID().toString(true);
        //尝试从缓存中获取数据
        log.info("尝试从缓存中获取数据.......");
        String data = redisTemplate.opsForValue().get(key);
        //缓存中没有拿到数据，去数据库中拿取数据
        if (StrUtil.isEmpty(data)) {
            log.info("缓存数据为空，尝试获取锁去查询数据库");
            try {
                Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(lockKey, lockValue, 30, TimeUnit.SECONDS);
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
                //释放锁前先判断是否还是自己加的那个锁
                if (lockValue.equals(redisTemplate.opsForValue().get(lockKey))) {
                    //锁用完后释放
                    redisTemplate.delete(lockKey);
                }
            }
        } else {
            log.info("缓存中有数据，直接获取");
            data = "从缓存中查询出来的数据";
        }
        log.info("查询数据：{}", data);
    }
}
