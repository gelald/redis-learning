package com.github.gelald.caffeine.controller;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.gelald.caffeine.dto.UserDTO;
import com.github.gelald.caffeine.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "Caffeine-Spring", description = "Caffeine Spring Cache use cases")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private CaffeineCacheManager caffeineCacheManager;

    @PostMapping("/save")
    @CachePut(value = "user", key = "#p0.id")
    @Operation(summary = "Save", description = "Save user and put data into cache case")
    public UserDTO saveUser(@RequestBody UserDTO userDTO) {
        Long id = userService.save(userDTO);
        log.info("put user into cache successfully, key: {}", id);
        return userDTO;
    }

    @GetMapping("/get")
    @Cacheable(value = "user", key = "#p0", cacheManager = "caffeineCacheManager")
    @Operation(summary = "Get", description = "Get user from cache or DAO")
    public UserDTO getUser(@RequestParam("id") Long id) {
        log.info("if this log is printed,  it indicates that no data has been retrieved from the cache");
        UserDTO userDTO = userService.getUserById(id);
        return userDTO;
    }

    @DeleteMapping("/remove")
    @CacheEvict(value = "user", key = "#p0")
    @Operation(summary = "Remove", description = "Remove user from cache and DAO")
    public Long removeUser(@RequestParam("id") Long id) {
        userService.remove(id);
        log.info("evict user cache successfully, id: {}", id);
        return id;
    }

    @GetMapping("/list-all-cache")
    @Operation(summary = "List", description = "List all data from cache")
    public Map<Object, Object> getCacheNames() {
        CaffeineCache cache = (CaffeineCache) caffeineCacheManager.getCache("user");
        assert cache != null;
        Cache<Object, Object> nativeCache = cache.getNativeCache();
        ConcurrentMap<Object, Object> map = nativeCache.asMap();
        for (Map.Entry<Object, Object> entry : map.entrySet()) {
            log.info("cache key: {}, value: {}", entry.getKey(), entry.getValue());
        }
        return map;
    }
}
