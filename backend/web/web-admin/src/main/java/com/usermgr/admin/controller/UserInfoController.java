package com.usermgr.admin.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usermgr.admin.security.AdminOnly;
import com.usermgr.common.result.Result;
import com.usermgr.model.entity.UserInfo;
import com.usermgr.service.mapper.UserInfoMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Tag(name = "租客管理")
@RestController
@RequestMapping("/admin/client")
public class UserInfoController {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @AdminOnly
    @Operation(summary = "分页查询租客")
    @GetMapping("/page")
    public Result<IPage<UserInfo>> page(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize,
            @RequestParam(required = false) String keyword) {
        Page<UserInfo> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<UserInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(UserInfo::getNickname, keyword)
                   .or()
                   .like(UserInfo::getPhone, keyword);
        }
        wrapper.orderByDesc(UserInfo::getCreateTime);
        return Result.ok(userInfoMapper.selectPage(page, wrapper));
    }

    @AdminOnly
    @Operation(summary = "租客详情")
    @GetMapping("/{id}")
    public Result<UserInfo> getById(@PathVariable Long id) {
        return Result.ok(userInfoMapper.selectById(id));
    }

}
