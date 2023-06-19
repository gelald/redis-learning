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
 * 代码思路：为了解决同一时刻大量线程都没有从缓存中查询到数据而直接访问数据库的问题，
 * 引入Redis分布式锁，只有拿到了分布式锁的请求才可以去访问数据库，最后在finally中释放锁，
 * 没有拿到锁的线程等待，等待完成后重新去获取锁
 * 同一时刻，请求同一条数据只有一个用户可以访问数据库，那么这个时候数据库就不会有那么大的压力
 * </p>
 *
 * <p>
 * 存在问题：如果拿到锁，正在执行业务逻辑，程序挂了，就没法释放锁了，其他请求就再也没办法拿到锁了
 * </p>
 *
 * @author WuYingBin
 * date: 2023/6/19
 */
@Slf4j
@Service
public class RedisLockV1 implements RedisLock {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void getData(Long dataId) throws Exception {
        //组装缓存key
        String key = RedisConstant.CACHE_KEY_PREFIX + dataId;
        //尝试从缓存中获取数据
        log.info("尝试从缓存中获取数据.......");
        String data = redisTemplate.opsForValue().get(key);
        //缓存中没有拿到数据，去数据库中拿取数据
        if (StrUtil.isEmpty(data)) {
            log.info("缓存数据为空，尝试获取锁去查询数据库");
            try {
                Boolean ifAbsent = redisTemplate.opsForValue().setIfAbsent(RedisConstant.LOCK_KEY_PREFIX, "lock");
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
                redisTemplate.delete(RedisConstant.LOCK_KEY_PREFIX);
            }
        } else {
            log.info("缓存中有数据，直接获取");
            data = "从缓存中查询出来的数据";
        }
        log.info("查询数据：{}", data);
    }
}
