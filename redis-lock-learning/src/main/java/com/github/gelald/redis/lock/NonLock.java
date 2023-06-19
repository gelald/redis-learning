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
 * 代码思路：根据商品id查询时，先去缓存中拿取数据，如果缓存中获取到数据，直接将该数据返回；
 * 否则去数据库中获取数据，当从数据库获取到数据时，会将数据存放进缓存且将数据返回。
 * </p>
 *
 * <p>
 * 上述思路看似没有问题，但是在高并发中存在如下问题：
 * 当大家同时从缓存中没有获取到时，那么大家都去数据库中获取数据，那么高并发情况下会将数据库直接打宕机。
 * </p>
 *
 * @author WuYingBin
 * date: 2023/6/19
 */
@Slf4j
@Service
public class NonLock implements RedisLock {
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
            //从数据库中获取数据
            log.info("缓存数据为空，正在从数据库中获取数据......");
            TimeUnit.MILLISECONDS.sleep(300);
            data = "从数据库查询出来的数据";
            //把数据库中查询出来的数据放入缓存中
            redisTemplate.opsForValue().set(key, data);
            log.info("把数据库中查询出来的数据放入缓存中");
        } else {
            log.info("缓存中有数据，直接获取");
            data = "从缓存中查询出来的数据";
        }
        log.info("查询数据：{}", data);
    }
}
