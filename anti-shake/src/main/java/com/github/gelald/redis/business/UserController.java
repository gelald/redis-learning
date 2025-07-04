package com.github.gelald.redis.business;

import com.github.gelald.redis.annotation.RequestCache;
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
public class UserController {

    @RequestCache(prefix = "RL_")
    @PostMapping("/add")
    public ResponseEntity<String> add(@RequestBody AddReq addReq) {
        log.info("添加用户成功: {}", addReq);
        return ResponseEntity.ok("添加用户成功");
    }
}
