package com.usermgr.admin.security;

import com.usermgr.model.entity.SystemUser;
import com.usermgr.model.enums.SystemUserType;
import com.usermgr.service.mapper.SystemUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 鉴权实现。
 *
 * 当前: 判断 system_user.type == ADMIN
 * 将来切 RBAC: 替换本类实现 → 查 sys_user_role → sys_role_menu → 权限表达式求值
 *
 * @see UPGRADE_REGISTER.md 第 1 条
 */
@Component
public class AuthorityChecker {

    @Autowired
    private SystemUserMapper systemUserMapper;

    /**
     * @param userId 当前登录用户 ID
     * @return true=放行, false=403
     */
    public boolean check(Long userId) {
        SystemUser user = systemUserMapper.selectById(userId);
        if (user == null) return false;

        // 将来替换为: 查 RBAC 五表
        return SystemUserType.ADMIN.equals(user.getType());
    }

}
