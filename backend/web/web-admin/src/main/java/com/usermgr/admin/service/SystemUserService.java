package com.usermgr.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usermgr.admin.vo.system.SystemUserInfoVo;
import com.usermgr.admin.vo.system.SystemUserItemVo;
import com.usermgr.admin.vo.system.SystemUserQueryVo;

public interface SystemUserService {

    IPage<SystemUserItemVo> pageUsers(Page<SystemUserItemVo> page, SystemUserQueryVo queryVo);

    SystemUserInfoVo getUserById(Long id);

    void saveUser(SystemUserInfoVo vo);

    void updateUser(SystemUserInfoVo vo);

    void deleteUser(Long userId);

    void kickUser(Long userId);
}
