package com.github.gelald.redis.controller;

import cn.hutool.core.util.RandomUtil;
import com.github.gelald.redis.annotation.DoubleDeleteCache;
import com.github.gelald.redis.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "User test cases")
public class UserController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Operation(summary = "Get user", description = "Get user by id case")
    @GetMapping("/get/{id}")
    public ResponseEntity<User> get(@PathVariable("id") Long id) {
        String key = String.format("user::%d", id);
        if (redisTemplate.hasKey(key)) {
            User user = (User) redisTemplate.opsForValue().get(key);
            log.info("load user from redis successfully: {}", user);
            return ResponseEntity.ok(user);
        }
        // User user = userRepository.getReferenceById(id);
        User user = new User();
        user.setId(id);
        user.setUsername(RandomUtil.randomString(5));
        log.info("user get from db: {}", user);
        redisTemplate.opsForValue().set(String.format("user::%d", id), user);
        log.info("put user into redis successfully");
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "Update user", description = "Update user info case")
    @DoubleDeleteCache(name = "user")
    @PostMapping("/update")
    public ResponseEntity<Boolean> updateData(@RequestBody User user) {
        // int update = userRepository.updateUsernameById(user.getUsername(), user.getId());
        log.info("update user successfully: {}", user);
        return ResponseEntity.ok(true);
    }
}
