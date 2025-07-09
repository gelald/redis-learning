package com.github.gelald.redis.business;

import com.github.gelald.redis.annotation.RequestCache;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ngwingbun
 * date: 2024/7/20
 */
@Slf4j
@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "User test cases")
public class UserController {

    @RequestCache(prefix = "RL_")
    @PostMapping("/add")
    @Operation(summary = "Add User", description = "Add User case")
    public ResponseEntity<String> add(@RequestBody CreateUserReq createUserReq) {
        log.info("添加用户成功: {}", createUserReq);
        return ResponseEntity.ok("添加用户成功");
    }
}
