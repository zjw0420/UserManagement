package com.usermgr.admin.controller.apartment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.usermgr.admin.security.AdminOnly;
import com.usermgr.common.result.Result;
import com.usermgr.model.entity.AttrKey;
import com.usermgr.model.entity.AttrValue;
import com.usermgr.service.business.AttrKeyService;
import com.usermgr.service.business.AttrValueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "属性管理")
@RestController
@RequestMapping("/admin/attr")
public class AttrController {

    @Autowired
    private AttrKeyService attrKeyService;

    @Autowired
    private AttrValueService attrValueService;

    // ===== Key =====
    @AdminOnly @Operation(summary = "属性key列表")
    @GetMapping("/key/list")
    public Result<List<AttrKey>> keyList() {
        return Result.ok(attrKeyService.list());
    }

    @AdminOnly @Operation(summary = "新增/修改属性key")
    @PostMapping("/key/save")
    public Result<Void> keySave(@RequestBody AttrKey entity) {
        attrKeyService.saveOrUpdate(entity);
        return Result.ok();
    }

    @AdminOnly @Operation(summary = "删除属性key")
    @DeleteMapping("/key/{id}")
    public Result<Void> keyDelete(@PathVariable Long id) {
        // 一并删除关联的 value
        attrValueService.remove(new LambdaQueryWrapper<AttrValue>().eq(AttrValue::getAttrKeyId, id));
        attrKeyService.removeById(id);
        return Result.ok();
    }

    // ===== Value =====
    @AdminOnly @Operation(summary = "属性值列表")
    @GetMapping("/value/list")
    public Result<List<AttrValue>> valueList(@RequestParam Long keyId) {
        return Result.ok(attrValueService.list(
                new LambdaQueryWrapper<AttrValue>().eq(AttrValue::getAttrKeyId, keyId)));
    }

    @AdminOnly @Operation(summary = "新增/修改属性值")
    @PostMapping("/value/save")
    public Result<Void> valueSave(@RequestBody AttrValue entity) {
        attrValueService.saveOrUpdate(entity);
        return Result.ok();
    }

    @AdminOnly @Operation(summary = "删除属性值")
    @DeleteMapping("/value/{id}")
    public Result<Void> valueDelete(@PathVariable Long id) {
        attrValueService.removeById(id);
        return Result.ok();
    }
}
