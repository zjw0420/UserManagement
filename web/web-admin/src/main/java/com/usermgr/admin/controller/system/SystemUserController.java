package com.usermgr.admin.controller.system;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usermgr.admin.security.AdminOnly;
import com.usermgr.admin.service.SystemUserService;
import com.usermgr.admin.vo.system.SystemUserInfoVo;
import com.usermgr.admin.vo.system.SystemUserItemVo;
import com.usermgr.admin.vo.system.SystemUserQueryVo;
import com.usermgr.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户管理")
@RestController
@RequestMapping("/admin/user")
public class SystemUserController {

    @Autowired
    private SystemUserService systemUserService;

    @AdminOnly
    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public Result<IPage<SystemUserItemVo>> page(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize,
            SystemUserQueryVo queryVo) {
        Page<SystemUserItemVo> page = new Page<>(pageNum, pageSize);
        return Result.ok(systemUserService.pageUsers(page, queryVo));
    }

    @AdminOnly
    @Operation(summary = "用户详情")
    @GetMapping("/{id}")
    public Result<SystemUserInfoVo> getById(@PathVariable Long id) {
        return Result.ok(systemUserService.getUserById(id));
    }

    @AdminOnly
    @Operation(summary = "新增用户")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody SystemUserInfoVo vo) {
        systemUserService.saveUser(vo);
        return Result.ok();
    }

    @AdminOnly
    @Operation(summary = "修改用户")
    @PutMapping("/update")
    public Result<Void> update(@RequestBody SystemUserInfoVo vo) {
        systemUserService.updateUser(vo);
        return Result.ok();
    }

    @AdminOnly
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        systemUserService.deleteUser(id);
        return Result.ok();
    }

    @AdminOnly
    @Operation(summary = "踢出用户")
    @PostMapping("/kick/{id}")
    public Result<Void> kick(@PathVariable Long id) {
        systemUserService.kickUser(id);
        return Result.ok();
    }

}
