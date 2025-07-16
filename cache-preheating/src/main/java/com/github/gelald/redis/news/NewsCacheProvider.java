package com.github.gelald.redis.news;

import com.github.gelald.redis.core.CacheProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class NewsCacheProvider implements CacheProvider {
    private static final String NEWS_KEY = "news";
    private final RedisTemplate<String, Object> redisTemplate;

    public NewsCacheProvider(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void init() {
        News news1 = new News();
        news1.setId(1L);
        news1.setTitle("测试新闻1");
        news1.setUrl("https://www.baidu.com");

        News news2 = new News();
        news2.setId(2L);
        news2.setTitle("测试新闻2");
        news2.setUrl("https://www.bing.com");

        News news3 = new News();
        news3.setId(3L);
        news3.setTitle("测试新闻3");
        news3.setUrl("https://www.google.com");

        List<News> newsList = List.of(news1, news2, news3);
        Boolean result = redisTemplate.opsForValue().setIfAbsent(NEWS_KEY, newsList, 30, TimeUnit.SECONDS);
        if (Boolean.TRUE.equals(result)) {
            log.info("{} cache init successfully, size: {}", NEWS_KEY, newsList.size());
        } else {
            log.info("{} cache init unsuccessfully, maybe cache is exists", NEWS_KEY);
        }
    }

    @Override
    public Object get() {
        if (!redisTemplate.hasKey(NEWS_KEY)) {
            log.info("cache doesn't contain {} cache, now reload it", NEWS_KEY);
            reload();
        }
        log.info("get {} from cache", NEWS_KEY);
        return redisTemplate.opsForValue().get(NEWS_KEY);
    }

    @Override
    public void clear() {
        log.info("clear {} from cache", NEWS_KEY);
        redisTemplate.delete(NEWS_KEY);
    }
}
