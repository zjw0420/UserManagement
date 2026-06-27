package com.usermgr.admin.controller.system;

import com.usermgr.admin.security.AdminOnly;
import com.usermgr.admin.service.SystemPostService;
import com.usermgr.common.result.Result;
import com.usermgr.model.entity.SystemPost;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "岗位管理")
@RestController
@RequestMapping("/admin/post")
public class SystemPostController {

    @Autowired
    private SystemPostService systemPostService;

    @AdminOnly
    @Operation(summary = "岗位列表")
    @GetMapping("/list")
    public Result<List<SystemPost>> list() {
        return Result.ok(systemPostService.listAll());
    }

    @AdminOnly
    @Operation(summary = "新增/修改岗位")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody SystemPost post) {
        systemPostService.save(post);
        return Result.ok();
    }

    @AdminOnly
    @Operation(summary = "删除岗位")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        systemPostService.delete(id);
        return Result.ok();
    }

}
