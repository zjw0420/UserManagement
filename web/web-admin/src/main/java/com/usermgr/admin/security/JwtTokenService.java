package com.usermgr.admin.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * JWT Token 版本号管理 (方案 B: tokenVersion)。
 *
 * Redis Key: user:{userId}:tokenVersion → 整数
 * 踢人 = INCR 版本号 → 所有旧 JWT 同步失效
 *
 * @see UPGRADE_REGISTER.md 第 2 条
 */
@Slf4j
@Component
public class JwtTokenService {

    private static final String VERSION_KEY_PREFIX = "user:";
    private static final String VERSION_KEY_SUFFIX = ":tokenVersion";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    /**
     * 签发前调用: 取当前版本号，不存在则初始化为 1。
     */
    public int getOrInitVersion(Long userId) {
        String key = versionKey(userId);
        String val = redisTemplate.opsForValue().get(key);
        if (val == null) {
            redisTemplate.opsForValue().set(key, "1", 10, TimeUnit.DAYS);
            return 1;
        }
        try {
            return Integer.parseInt(val);
        } catch (NumberFormatException e) {
            redisTemplate.opsForValue().set(key, "1", 10, TimeUnit.DAYS);
            return 1;
        }
    }

    /**
     * 校验: JWT 里的版本号和 Redis 里的版本号是否一致。
     */
    public boolean isVersionValid(Long userId, int tokenVersion) {
        int current = getOrInitVersion(userId);
        return current == tokenVersion;
    }

    /**
     * 踢人 / 换密码: INCR 版本号，O(1)。
     */
    public void kickAll(Long userId) {
        redisTemplate.opsForValue().increment(versionKey(userId));
        log.info("已踢出用户 {} 的所有设备", userId);
    }

    private String versionKey(Long userId) {
        return VERSION_KEY_PREFIX + userId + VERSION_KEY_SUFFIX;
    }

}
