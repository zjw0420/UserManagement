package com.usermgr.admin.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // 手动注入配置（绕过 @Value 注解）
        ReflectionTestUtils.setField(jwtUtil, "secret",
                "TestSecret-ForUnitTest-MustBeAtLeast256Bits-LongEnough!");
        ReflectionTestUtils.setField(jwtUtil, "expirationMs", 60_000L); // 1 分钟
        jwtUtil.init();
    }

    @Test
    @DisplayName("签发 + 解析 → userId 一致")
    void shouldGenerateAndParse() {
        String token = jwtUtil.generate(1L, 1);
        assertNotNull(token);

        Long userId = jwtUtil.getUserId(token);
        assertEquals(1L, userId);

        int version = jwtUtil.getTokenVersion(token);
        assertEquals(1, version);
    }

    @Test
    @DisplayName("无效 token → 返回 null")
    void shouldReturnNullForInvalidToken() {
        assertNull(jwtUtil.getUserId("not.a.valid.jwt"));
        assertNull(jwtUtil.getUserId(""));
        assertNull(jwtUtil.getUserId(null));
    }

    @Test
    @DisplayName("version 提取正确")
    void shouldExtractVersion() {
        String token = jwtUtil.generate(42L, 99);
        assertEquals(99, jwtUtil.getTokenVersion(token));
        assertEquals(42L, jwtUtil.getUserId(token));
    }
}
