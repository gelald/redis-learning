package com.github.gelald.redis.entity;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 429999L;
    private Long id;
    private String username;
}
