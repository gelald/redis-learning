package com.github.gelald.redis.core.runner;

import com.github.gelald.redis.core.CacheProvider;
import com.github.gelald.redis.util.ApplicationContextUtil;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConditionalOnProperty(name = "cache.init.enable", havingValue = "true")
public class CachePreHeatingRunner implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        ApplicationContext context = ApplicationContextUtil.getContext();
        Map<String, CacheProvider> cacheProviderMap = context.getBeansOfType(CacheProvider.class);
        for (Map.Entry<String, CacheProvider> entry : cacheProviderMap.entrySet()) {
            CacheProvider cacheProvider = context.getBean(entry.getValue().getClass());
            cacheProvider.init();
        }
    }
}
