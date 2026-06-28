package com.usermgr.admin.service;

import com.usermgr.admin.security.JwtTokenService;
import com.usermgr.admin.security.JwtUtil;
import com.usermgr.admin.security.LoginRateLimiter;
import com.usermgr.admin.service.impl.LoginServiceImpl;
import com.usermgr.admin.vo.login.LoginVo;
import com.usermgr.common.exception.BusinessException;
import com.usermgr.common.result.ResultCodeEnum;
import com.usermgr.model.entity.SystemUser;
import com.usermgr.service.mapper.SystemUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class LoginServiceImplTest {

    @Mock private SystemUserMapper systemUserMapper;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private JwtTokenService jwtTokenService;
    @Mock private LoginRateLimiter rateLimiter;
    @Mock private RedisTemplate<String, String> redisTemplate;
    @Mock private ValueOperations<String, String> valueOps;

    @InjectMocks
    private LoginServiceImpl loginService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
    }

    private LoginVo makeLogin(String captchaCode) {
        LoginVo vo = new LoginVo();
        vo.setUsername("admin");
        vo.setPassword("123456");
        vo.setCaptchaKey("test-key");
        vo.setCaptchaCode(captchaCode);
        return vo;
    }

    @Test
    @DisplayName("验证码错误 → 抛异常")
    void shouldFailOnWrongCaptcha() {
        when(valueOps.get("captcha:test-key")).thenReturn("xyz");

        BusinessException ex = assertThrows(BusinessException.class,
                () -> loginService.login(makeLogin("wrong"), "127.0.0.1"));
        assertEquals(ResultCodeEnum.CAPTCHA_ERROR.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("用户不存在 → 抛异常")
    void shouldFailOnUserNotFound() {
        when(valueOps.get("captcha:test-key")).thenReturn("abcd");
        when(systemUserMapper.selectOne(any())).thenReturn(null);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> loginService.login(makeLogin("abcd"), "127.0.0.1"));
        assertEquals(ResultCodeEnum.USER_NOT_FOUND.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("密码错误 → 抛异常")
    void shouldFailOnWrongPassword() {
        when(valueOps.get("captcha:test-key")).thenReturn("abcd");
        SystemUser user = new SystemUser();
        user.setId(1L);
        user.setPassword("encrypted-real-password");
        user.setStatus((com.usermgr.model.enums.BaseStatus) null); // not null for status check
        when(systemUserMapper.selectOne(any())).thenReturn(user);
        when(passwordEncoder.matches(eq("123456"), eq("encrypted-real-password"))).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> loginService.login(makeLogin("abcd"), "127.0.0.1"));
        assertEquals(ResultCodeEnum.PASSWORD_ERROR.getCode(), ex.getCode());
    }

    @Test
    @DisplayName("正常登录 → 返回 token")
    void shouldReturnTokenOnSuccess() {
        when(valueOps.get("captcha:test-key")).thenReturn("abcd");
        SystemUser user = new SystemUser();
        user.setId(1L);
        user.setPassword("encrypted-real-password");
        user.setStatus(null);
        when(systemUserMapper.selectOne(any())).thenReturn(user);
        when(passwordEncoder.matches(eq("123456"), eq("encrypted-real-password"))).thenReturn(true);
        when(jwtTokenService.getOrInitVersion(1L)).thenReturn(1);
        when(jwtUtil.generate(1L, 1)).thenReturn("fake-jwt-token");

        String result = loginService.login(makeLogin("abcd"), "127.0.0.1");
        assertNotNull(result);
        assertEquals("fake-jwt-token", result);
    }
}
