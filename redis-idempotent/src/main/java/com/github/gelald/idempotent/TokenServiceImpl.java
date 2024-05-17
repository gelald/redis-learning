package com.github.gelald.idempotent;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TokenServiceImpl implements TokenService {
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public String getToken() {
        // 随机生成一串UUID作为token
        String token = UUID.randomUUID().toString(true);
        // 存进Redis中
        redisTemplate.opsForValue().set("testToken", token);
        return token;
    }

    @Override
    public boolean checkToken(HttpServletRequest request) {
        // 从请求头中获取token
        String token = request.getHeader("token");
        // 判断是否为空
        if (StrUtil.isEmpty(token)) {
            // token为空则抛出异常
            throw new CustomException("请求头未携带token！");
        }

        // LUA脚本保证获取token，校验token，删除token是原子性的
        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        // 调用execute执行，第一个参数传入脚本，第二个参数传入key（这里是之前写死的testToken），第三个参数传入AVG（这里是请求头携带的token）
        // 该lua脚本会对比 传入的key获取到的value是否与请求头获取的token一致，一致说明存在，删除key的数据，不一致返回0
        Long res = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), List.of("testToken"),  token);

        // 不一致，说明不存在，抛出重复异常
        if (res == 0) {
            throw new CustomException("请求重复提交！");
        }

        // 执行业务代码
        System.out.println("执行业务代码");

        return true;
    }
}
