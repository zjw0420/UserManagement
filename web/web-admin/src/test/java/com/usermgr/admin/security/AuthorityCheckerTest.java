package com.usermgr.admin.security;

import com.usermgr.model.entity.SystemUser;
import com.usermgr.model.enums.SystemUserType;
import com.usermgr.service.mapper.SystemUserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorityCheckerTest {

    @Mock
    private SystemUserMapper systemUserMapper;

    @InjectMocks
    private AuthorityChecker authorityChecker;

    @BeforeEach
    void setUp() {
        // Mockito 自动注入 @Mock → @InjectMocks
    }

    @Test
    @DisplayName("管理员 → 放行")
    void adminShouldPass() {
        SystemUser admin = new SystemUser();
        admin.setType(SystemUserType.ADMIN);
        when(systemUserMapper.selectById(1L)).thenReturn(admin);

        assertTrue(authorityChecker.check(1L));
    }

    @Test
    @DisplayName("普通用户 → 拒绝")
    void commonUserShouldFail() {
        SystemUser user = new SystemUser();
        user.setType(SystemUserType.COMMON);
        when(systemUserMapper.selectById(2L)).thenReturn(user);

        assertFalse(authorityChecker.check(2L));
    }

    @Test
    @DisplayName("用户不存在 → 拒绝")
    void nullUserShouldFail() {
        when(systemUserMapper.selectById(999L)).thenReturn(null);

        assertFalse(authorityChecker.check(999L));
    }
}
