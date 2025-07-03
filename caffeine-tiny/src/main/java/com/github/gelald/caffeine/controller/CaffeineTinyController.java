package com.github.gelald.caffeine.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.gelald.caffeine.dto.CacheDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequestMapping("/caffeine-tiny")
@Tag(name = "Caffeine", description = "Caffeine use cases")
public class CaffeineTinyController {
    @Autowired
    @Qualifier("caffeineCache")
    private Cache<String, Object> caffeineCache;

    @PostMapping("/put")
    @Operation(summary = "Put", description = "Put data into cache case")
    public String put(@Valid @RequestBody CacheDTO cacheDTO) {
        String key = cacheDTO.getKey();
        Object value = cacheDTO.getValue();
        caffeineCache.put(key, value);
        log.info("[put] put data into caffeine successfully, key: {}, value: {}", key, value);
        return "success";
    }

    @PostMapping("/putAll")
    @Operation(summary = "Batch Put", description = "Batch put data into cache case")
    public String putAll(@Valid @RequestBody List<CacheDTO> cacheDTOList) {
        if (CollUtil.isEmpty(cacheDTOList)) {
            log.error("[putAll] cacheDTOList is empty");
            return "failed";
        }
        Map<String, Object> map = cacheDTOList.stream().collect(Collectors.toMap(
                CacheDTO::getKey, CacheDTO::getValue, (oldValue, newValue) -> newValue
        ));
        caffeineCache.putAll(map);
        log.info("[putAll] batch put data into caffeine successfully, data: {}", map);
        return "success";
    }

    @GetMapping("/get/{key}")
    @Operation(summary = "Get", description = "Get data from cache case")
    public Object getByKey(@PathVariable("key") String key) {
        Object value = caffeineCache.getIfPresent(key);
        if (ObjectUtil.isEmpty(value)) {
            log.info("[getByKey] get a null value from caffeine, key: {}", key);
        } else {
            log.info("[getByKey] get value: {} from caffeine, key: {}", value, key);
        }
        return value;
    }

    @GetMapping("/get")
    @Operation(summary = "Batch Get", description = "Batch get data from cache case")
    public Map<String, Object> getByKeys(@RequestParam("keys") List<String> keys) {
        Map<String, Object> values = caffeineCache.getAllPresent(keys);
        if (CollUtil.isEmpty(values)) {
            log.info("[getByKeys] can not get data from caffeine, keys: {}", keys);
        } else {
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                log.info("[getByKeys] get value: {} from caffeine, key: {}", entry.getValue(), entry.getKey());
            }
        }
        return values;
    }

    @GetMapping("/getAll")
    @Operation(summary = "Get All", description = "Get all data from cache case")
    public Map<String, Object> getAll() {
        Map<String, Object> values = caffeineCache.asMap();
        if (CollUtil.isEmpty(values)) {
            log.info("[getAll] can not get any value from caffeine, because caffeine is empty");
        } else {
            for (Map.Entry<String, Object> entry : values.entrySet()) {
                log.info("[getAll] get value: {} from caffeine, key: {}", entry.getValue(), entry.getKey());
            }
        }
        return values;
    }

    @DeleteMapping("/delete/{key}")
    @Operation(summary = "Delete", description = "Delete data from cache case")
    public String deleteByKey(@PathVariable("key") String key) {
        caffeineCache.invalidate(key);
        log.info("[deleteByKey] remove cache from caffeine, key: {}", key);
        return "success";
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Batch Delete", description = "Batch delete data from cache case")
    public String deleteByKeys(@RequestParam("keys") List<String> keys) {
        caffeineCache.invalidateAll(keys);
        log.info("[deleteByKeys] remove cache from caffeine, keys: {}", keys);
        return "success";
    }

    @DeleteMapping("/deleteAll")
    @Operation(summary = "Delete All", description = "Delete all data from cache case")
    public String deleteAll() {
        caffeineCache.invalidateAll();
        log.info("[deleteAll] remove all cache from caffeine");
        return "success";
    }
}
