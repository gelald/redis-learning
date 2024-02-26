package com.github.gelald.distribted.lock;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Edwin
 */
@Slf4j
@RestController
@RequestMapping("/")
public class TestController {
    @GetMapping("/testRedisLock")
    @RedisLockAnnotation(typeEnum = RedisLockTypeEnum.ONE, lockTime = 3)
    public void testRedisLock(@RequestParam("userId") Long userId) {
        try {
            log.info("睡眠执行前");
            Thread.sleep(10000);
            log.info("睡眠执行后");
        } catch (Exception e) {
            // log error
            log.error("has some error", e);
        }
    }
}
