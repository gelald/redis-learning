package com.github.gelald.redis.controller;

import cn.hutool.core.util.IdUtil;
import com.github.gelald.redis.dto.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/spring-cache")
@Tag(name = "Spring Cache", description = "Spring Cache use cases")
public class CacheController {

    // ============ Cacheable 缓存有直接返回，没有则把方法返回结果缓存起来 ============
    // key: 获取方法参数的方式 #p0/#p1 代表第一个第二个参数
    @Cacheable(value = "userDTO", key = "'p_' + #p0")
    @GetMapping("/get/{username}")
    @Operation(summary = "cache UserDTO data", description = "cache UserDTO data")
    public UserDTO getUserDTO(@PathVariable("username") String username) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(-1L);
        userDTO.setUsername(username);
        userDTO.setEmail("mock@mock.com");
        return userDTO;
    }

    @Cacheable(value = "page", key = "#p0 + '-' + #p1")
    @GetMapping("/page")
    @Operation(summary = "cache page data", description = "cache page data")
    public String getPageData(@RequestParam("page") Integer page,
                              @RequestParam("size") Integer size) {
        return "getPageData: " + page + "," + size + "-->" + IdUtil.randomUUID();
    }

    // condition: 只有满足条件才放入缓存，这里的条件是第一个参数(age)是偶数才缓存
    @Cacheable(value = "condition", key = "#p0", condition = "#p0 % 2 == 0")
    @GetMapping("/condition")
    public String getByCondition(@RequestParam("age") Integer age) {
        return "getByCondition: " + age + "-->" + IdUtil.randomUUID();
    }

    // unless: 只有不满足条件才放入缓存，这里的条件是第一个参数(age)是奇数(不是偶数)才缓存
    @Cacheable(value = "unless", key = "#p0", unless = "#p0 % 2 == 0")
    @GetMapping("/unless")
    public String getByUnless(@RequestParam("age") Integer age) {
        return "getByUnless: " + age + "-->" + IdUtil.randomUUID();
    }
    // ============ Cacheable 缓存有直接返回，没有则把方法返回结果缓存起来 ============


    // ===================== CachePut 把方法返回结果缓存起来 =====================
    @CachePut(value = "userDTO", key = "'p_' + #p0.username")
    @PostMapping("/put")
    public UserDTO putUserDTO(@Validated @RequestBody UserDTO userDTO) {
        userDTO.setId(IdUtil.getSnowflake().nextId());
        log.info("save UserDTO: {}", userDTO);
        return userDTO;
    }
    // ===================== CachePut 把方法返回结果缓存起来 =====================


    // =================== CacheEvict 根据key删除对应的缓存值 ===================
    @CacheEvict(value = "userDTO", key = "'p_' + #p0")
    @DeleteMapping("/evict")
    public String evict(@RequestParam("username") String username) {
        return "evict:" + username + "-->" + IdUtil.randomUUID();
    }

    // allEntries: 是否删除缓存中的所有条目. 默认情况下, 只移除相关键下的值。如果设置了true，就无需设置key
    // beforeInvocation: 是否在调用之前就删除缓存值
    @CacheEvict(value = "page", allEntries = true, beforeInvocation = true)
    @DeleteMapping("/delete/{id}")
    public Object deleteById(@PathVariable("id") Long id) {
        return "deleteById" + id + "-->" + IdUtil.randomUUID();
    }
    // =================== CacheEvict 根据key删除对应的缓存值 ===================


    // =========================== Caching 组合操作 ===========================
    @Caching(cacheable = @Cacheable(value = "userDTO", key = "'p_' + #p0"),
            evict = @CacheEvict(value = "condition", key = "#p1"))
    @GetMapping("/caching")
    public String caching(@RequestParam("username") String username, @RequestParam("age") Integer age) {
        return "caching " + username + "," + age + "-->" + IdUtil.randomUUID();
    }
    // =========================== Caching 组合操作 ===========================
}
