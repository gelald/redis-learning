package com.github.gelald.redis.news;

import lombok.Data;

@Data
public class News {
    private Long id;
    private String title;
    private String url;
}
