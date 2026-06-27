package com.usermgr.admin.controller.apartment;

import com.usermgr.admin.security.AdminOnly;
import com.usermgr.common.result.Result;
import com.usermgr.model.entity.LabelInfo;
import com.usermgr.service.business.LabelInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "标签管理")
@RestController
@RequestMapping("/admin/label")
public class LabelController {

    @Autowired
    private LabelInfoService labelInfoService;

    @AdminOnly
    @Operation(summary = "标签列表")
    @GetMapping("/list")
    public Result<List<LabelInfo>> list() {
        return Result.ok(labelInfoService.list());
    }

    @AdminOnly
    @Operation(summary = "标签详情")
    @GetMapping("/{id}")
    public Result<LabelInfo> getById(@PathVariable Long id) {
        return Result.ok(labelInfoService.getById(id));
    }

    @AdminOnly
    @Operation(summary = "新增/修改标签")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody LabelInfo entity) {
        labelInfoService.saveOrUpdate(entity);
        return Result.ok();
    }

    @AdminOnly
    @Operation(summary = "删除标签")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        labelInfoService.removeById(id);
        return Result.ok();
    }
}
