package com.github.gelald.redis.core.handler;

import com.github.gelald.redis.annotation.RequestCache;
import com.github.gelald.redis.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.data.redis.connection.RedisStringCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.types.Expiration;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * 使用原生redis client来实现分布式锁进而避免重复提交
 *
 * @author ngwingbun
 * date: 2024/7/20
 */
@Slf4j
@Component
public class NativeRedisHandler {
    private static final String LOCK_VALUE = "locked";
    /**
     * 直接使用stringRedisTemplate避免处理泛型
     */
    private final StringRedisTemplate stringRedisTemplate;

    public NativeRedisHandler(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public Object process(ProceedingJoinPoint joinPoint, String lockKey, RequestCache requestLock) {
        // 使用RedisCallback接口执行set命令，设置锁键；设置额外选项：过期时间和SET_IF_ABSENT选项
        // 执行setnx指令
        final Boolean success = stringRedisTemplate.execute((RedisCallback<Boolean>) connection -> {
            RedisStringCommands stringCommands = connection.stringCommands();
            return stringCommands.set(
                    lockKey.getBytes(StandardCharsets.UTF_8),
                    LOCK_VALUE.getBytes(StandardCharsets.UTF_8),
                    Expiration.from(requestLock.expire(), requestLock.timeUnit()),
                    RedisStringCommands.SetOption.SET_IF_ABSENT);
        });
        // 如果执行不成功，那么说明同样的key在缓存中，当前参数还是防抖时间
        if (Boolean.FALSE.equals(success)) {
            throw new BusinessException(1, "您的操作太快了,请稍后重试");
        }
        // 执行成功说明当前参数通过了防抖检测，执行业务
        try {
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            throw new BusinessException(3, "系统异常");
        }
    }

    ///**
    //     * redis 锁成功标识常量
    //     */
    //    private static final Long RELEASE_SUCCESS = 1L;
    //    private static final String SET_IF_NOT_EXIST = "NX";
    //    private static final String SET_WITH_EXPIRE_TIME = "EX";
    //    private static final String LOCK_SUCCESS= "OK";
    //    /**
    //     * 加锁 Lua 表达式。
    //     */
    //    private static final String RELEASE_TRY_LOCK_LUA =
    //            "if redis.call('setNx',KEYS[1],ARGV[1]) == 1 then return redis.call('expire',KEYS[1],ARGV[2]) else return 0 end";
    //    /**
    //     * 解锁 Lua 表达式.
    //     */
    //    private static final String RELEASE_RELEASE_LOCK_LUA =
    //            "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
    //
    //    /**
    //     * 加锁
    //     * 支持重复，线程安全
    //     * 既然持有锁的线程崩溃，也不会发生死锁，因为锁到期会自动释放
    //     * @param lockKey    加锁键
    //     * @param userId     加锁客户端唯一标识（采用用户id, 需要把用户 id 转换为 String 类型）
    //     * @param expireTime 锁过期时间
    //     * @return OK 如果key被设置了
    //     */
    //    public boolean tryLock(String lockKey, String userId, long expireTime) {
    //        Jedis jedis = JedisUtils.getInstance().getJedis();
    //        try {
    //            jedis.select(JedisUtils.index);
    //            String result = jedis.set(lockKey, userId, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, expireTime);
    //            if (LOCK_SUCCESS.equals(result)) {
    //                return true;
    //            }
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        } finally {
    //            if (jedis != null)
    //                jedis.close();
    //        }
    //
    //        return false;
    //    }
    //
    //    /**
    //     * 解锁
    //     * 与 tryLock 相对应，用作释放锁
    //     * 解锁必须与加锁是同一人，其他人拿到锁也不可以解锁
    //     *
    //     * @param lockKey 加锁键
    //     * @param userId  解锁客户端唯一标识（采用用户id, 需要把用户 id 转换为 String 类型）
    //     * @return
    //     */
    //    public boolean releaseLock(String lockKey, String userId) {
    //        Jedis jedis = JedisUtils.getInstance().getJedis();
    //        try {
    //            jedis.select(JedisUtils.index);
    //            Object result = jedis.eval(RELEASE_RELEASE_LOCK_LUA, Collections.singletonList(lockKey), Collections.singletonList(userId));
    //            if (RELEASE_SUCCESS.equals(result)) {
    //                return true;
    //            }
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        } finally {
    //            if (jedis != null)
    //                jedis.close();
    //        }
    //
    //        return false;
    //    }
    //
    //作者：秃头哥编程
    //链接：https://juejin.cn/post/7041557262699069476
    //来源：稀土掘金
    //著作权归作者所有。商业转载请联系作者获得授权，非商业转载请注明出处。
}
