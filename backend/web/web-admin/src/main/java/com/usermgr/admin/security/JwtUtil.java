package com.usermgr.admin.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    private SecretKey key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 签发 JWT
     * @param userId      用户 ID
     * @param tokenVersion 当前 token 版本号 (来自 Redis)
     */
    public String generate(Long userId, int tokenVersion) {
        Date now = new Date();
        return Jwts.builder()
                .setSubject(userId.toString())
                .claim("version", tokenVersion)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * 解析 JWT (不验版本号，版本号由 JwtTokenService 负责比对)
     * @return null 表示解析失败
     */
    public Claims parse(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            log.debug("JWT 已过期: {}", e.getMessage());
            return null;
        } catch (MalformedJwtException | SignatureException | IllegalArgumentException e) {
            log.debug("JWT 无效: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从 token 中提取 userId
     */
    public Long getUserId(String token) {
        Claims claims = parse(token);
        if (claims == null) return null;
        return Long.parseLong(claims.getSubject());
    }

    /**
     * 从 token 中提取 tokenVersion
     */
    public int getTokenVersion(String token) {
        Claims claims = parse(token);
        if (claims == null) return -1;
        return claims.get("version", Integer.class);
    }

}
