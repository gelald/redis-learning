package com.github.gelald.redis.lock;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author WuYingBin
 * date: 2023/6/19
 */
@Slf4j
@Service
public class NonLock implements RedisLock {
    @Resource
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public void getData(Long dataId) {
        //组装缓存key
        String key = "data:cache:" + dataId;
        //尝试从缓存中获取数据
        log.info("尝试从缓存中获取数据.......");
        String data = redisTemplate.opsForValue().get(key);
        //缓存中没有拿到数据，去数据库中拿取数据
        if (StrUtil.isEmpty(data)) {
            //从数据库中获取数据
            log.info("缓存数据为空，正在从数据库中获取数据......");
            data = "从数据库查询出来的数据";
            //把数据库中查询出来的数据放入缓存中
            redisTemplate.opsForValue().set(key, data);
            log.info("把数据库中查询出来的数据放入缓存中");
        } else {
            log.info("缓存中有数据，直接获取");
            data = "从缓存中查询出来的数据";
        }
    }
}
