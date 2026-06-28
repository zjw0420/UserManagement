package com.usermgr.admin.security;

import com.usermgr.common.exception.BusinessException;
import com.usermgr.common.result.ResultCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 登录限流 (Redis 计数器)。
 *
 * 三层: IP 限流 → 账号限流 → 账号锁定（数据库 status=0）
 */
@Slf4j
@Component
public class LoginRateLimiter {

    private static final String IP_PREFIX = "login:ip:";
    private static final String ACCOUNT_PREFIX = "login:account:";
    private static final String LOCK_PREFIX = "login:lock:";

    private static final int IP_MAX_ATTEMPTS = 20;       // 同 IP 5 分钟内最多 20 次
    private static final int ACCOUNT_MAX_ATTEMPTS = 5;   // 同账号 30 分钟内最多 5 次
    private static final int LOCK_THRESHOLD = 20;         // 一天内 20 次失败 → 锁账号
    private static final int IP_WINDOW_MINUTES = 5;
    private static final int ACCOUNT_WINDOW_MINUTES = 30;
    private static final int LOCK_WINDOW_HOURS = 24;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 登录失败后调用。递增 IP 计数和账号计数。
     */
    public void recordFailed(String ip, String username) {
        String ipKey = IP_PREFIX + ip;
        String accountKey = ACCOUNT_PREFIX + username;

        redisTemplate.opsForValue().increment(ipKey);
        redisTemplate.expire(ipKey, IP_WINDOW_MINUTES, TimeUnit.MINUTES);

        redisTemplate.opsForValue().increment(accountKey);
        redisTemplate.expire(accountKey, ACCOUNT_WINDOW_MINUTES, TimeUnit.MINUTES);

        // 锁计数器: 一天内累计 20 次失败
        String lockKey = LOCK_PREFIX + username;
        redisTemplate.opsForValue().increment(lockKey);
        redisTemplate.expire(lockKey, LOCK_WINDOW_HOURS, TimeUnit.HOURS);
    }

    /**
     * 登录前检查。超限则抛异常。
     */
    public void checkRateLimit(String ip, String username) {
        // IP 限流
        String ipVal = redisTemplate.opsForValue().get(IP_PREFIX + ip);
        if (ipVal != null && Integer.parseInt(ipVal) >= IP_MAX_ATTEMPTS) {
            throw new BusinessException(ResultCodeEnum.RATE_LIMITED);
        }

        // 账号限流
        String accountVal = redisTemplate.opsForValue().get(ACCOUNT_PREFIX + username);
        if (accountVal != null && Integer.parseInt(accountVal) >= ACCOUNT_MAX_ATTEMPTS) {
            throw new BusinessException(ResultCodeEnum.RATE_LIMITED);
        }
    }

    /**
     * 检查是否需要锁账号（一天累计失败 20 次）。
     * @return true 表示应该锁定
     */
    public boolean shouldLockAccount(String username) {
        String lockVal = redisTemplate.opsForValue().get(LOCK_PREFIX + username);
        return lockVal != null && Integer.parseInt(lockVal) >= LOCK_THRESHOLD;
    }

    /**
     * 登录成功后清除该 IP 和账号的失败计数。
     */
    public void clearFailed(String ip, String username) {
        redisTemplate.delete(IP_PREFIX + ip);
        redisTemplate.delete(ACCOUNT_PREFIX + username);
    }

}
