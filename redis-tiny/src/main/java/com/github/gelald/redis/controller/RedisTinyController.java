package com.github.gelald.redis.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.gelald.redis.dto.CacheDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("/redis-tiny")
@Tag(name = "Redis", description = "Redis use cases")
public class RedisTinyController {
    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate<String, Object> redisTemplate;

    @PostMapping("/put")
    @Operation(summary = "Put", description = "Put data into cache case")
    public String put(@Valid @RequestBody CacheDTO cacheDTO) {
        String key = cacheDTO.getKey();
        Object value = cacheDTO.getValue();
        redisTemplate.opsForValue().set(key, value);
        log.info("[put] put data into redis successfully, key: {}, value: {}", key, value);
        return "success";
    }

    @PostMapping("/putAll")
    @Operation(summary = "Batch Put", description = "Batch put data into cache case")
    public String putAll(@Valid @RequestBody List<CacheDTO> cacheDTOList) {
        if (CollUtil.isEmpty(cacheDTOList)) {
            log.info("[putAll] cacheDTOList is empty");
            return "failed";
        }
        Map<String, Object> map = cacheDTOList.stream().collect(Collectors.toMap(
                CacheDTO::getKey, CacheDTO::getValue, (oldValue, newValue) -> newValue
        ));
        redisTemplate.opsForValue().multiSet(map);
        log.info("[putAll] batch put data into redis successfully, data: {}", map);
        return "success";
    }

    @GetMapping("/get/{key}")
    @Operation(summary = "Get", description = "Get data from cache case")
    public Object getByKey(@PathVariable("key") String key) {
        Object value = redisTemplate.opsForValue().get(key);
        if (ObjectUtil.isEmpty(value)) {
            log.info("[getByKey] get a null value from redis, key: {}", key);
        } else {
            log.info("[getByKey] get value: {} from redis, key: {}", value, key);
        }
        return value;
    }

    @GetMapping("/get")
    @Operation(summary = "Batch Get", description = "Batch get data from cache case")
    public Map<String, Object> getByKeys(@RequestParam("keys") List<String> keys) {
        Map<String, Object> map = new HashMap<>();
        if (CollUtil.isEmpty(keys)) {
            log.info("[getByKeys] keys is empty");
            return map;
        }
        List<Object> values = redisTemplate.opsForValue().multiGet(keys);
        if (CollUtil.isEmpty(values)) {
            log.info("[getByKeys] can not get data from redis, keys: {}", keys);
            return map;
        }
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = values.get(i);
            map.put(key, value);
            log.info("[getByKeys] get value: {} from redis, key: {}", value, key);
        }
        return map;
    }

    @GetMapping("/getAll")
    @Operation(summary = "Get All", description = "Get all data from cache case")
    public Map<String, Object> getAll() {
        Map<String, Object> map = new HashMap<>();
        List<String> keys = new ArrayList<>(redisTemplate.keys("*"));
        List<Object> values = redisTemplate.opsForValue().multiGet(keys);
        if (CollUtil.isEmpty(values)) {
            log.info("[getAll] can not get data from redis, keys: {}", keys);
            return map;
        }
        for (int i = 0; i < keys.size(); i++) {
            String key = keys.get(i);
            Object value = values.get(i);
            map.put(key, value);
            log.info("[getAll] get value: {} from redis, key: {}", value, key);
        }
        return map;
    }

    @DeleteMapping("/delete/{key}")
    @Operation(summary = "Delete", description = "Delete data from cache case")
    public String deleteByKey(@PathVariable("key") String key) {
        Boolean delete = redisTemplate.delete(key);
        if (delete) {
            log.info("[deleteByKey] remove data from redis, key: {}", key);
            return "success";
        } else {
            log.error("[deleteByKey] can not remove data from redis, key: {}", key);
            return "failed";
        }
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Batch Delete", description = "Batch delete data from cache case")
    public String deleteByKeys(@RequestParam("keys") List<String> keys) {
        Long deleteCount = redisTemplate.delete(keys);
        log.info("[deleteByKeys] remove data from redis, keys: {}, deleteCount: {}", keys, deleteCount);
        return "success";
    }

    @DeleteMapping("/deleteAll")
    @Operation(summary = "Delete All", description = "Delete all data from cache case")
    public String deleteAll() {
        List<String> keys = new ArrayList<>(redisTemplate.keys("*"));
        Long deleteCount = redisTemplate.delete(keys);
        log.info("[deleteAll] remove all data from redis");
        return "success";
    }
}
