package com.github.gelald.redis.news;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/news")
@Tag(name = "News", description = "News use cases")
public class NewsController {
    @Autowired
    private NewsCacheProvider newsCacheProvider;

    @GetMapping("/cache")
    @Operation(summary = "Get data from cache", description = "Get data from cache")
    public Object getCache() {
        Object  list = newsCacheProvider.get();
        return list;
    }
}
