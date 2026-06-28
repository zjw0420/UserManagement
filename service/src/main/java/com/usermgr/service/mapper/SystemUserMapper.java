package com.usermgr.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.usermgr.model.entity.SystemUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SystemUserMapper extends BaseMapper<SystemUser> {
}
