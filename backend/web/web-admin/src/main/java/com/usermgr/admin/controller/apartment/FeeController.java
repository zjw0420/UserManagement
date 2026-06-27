package com.usermgr.admin.controller.apartment;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.usermgr.admin.security.AdminOnly;
import com.usermgr.common.result.Result;
import com.usermgr.model.entity.FeeKey;
import com.usermgr.model.entity.FeeValue;
import com.usermgr.service.business.FeeKeyService;
import com.usermgr.service.business.FeeValueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "费用管理")
@RestController
@RequestMapping("/admin/fee")
public class FeeController {

    @Autowired
    private FeeKeyService feeKeyService;

    @Autowired
    private FeeValueService feeValueService;

    // ===== Key =====
    @AdminOnly @Operation(summary = "费用key列表")
    @GetMapping("/key/list")
    public Result<List<FeeKey>> keyList() {
        return Result.ok(feeKeyService.list());
    }

    @AdminOnly @Operation(summary = "新增/修改费用key")
    @PostMapping("/key/save")
    public Result<Void> keySave(@RequestBody FeeKey entity) {
        feeKeyService.saveOrUpdate(entity);
        return Result.ok();
    }

    @AdminOnly @Operation(summary = "删除费用key")
    @DeleteMapping("/key/{id}")
    public Result<Void> keyDelete(@PathVariable Long id) {
        feeValueService.remove(new LambdaQueryWrapper<FeeValue>().eq(FeeValue::getFeeKeyId, id));
        feeKeyService.removeById(id);
        return Result.ok();
    }

    // ===== Value =====
    @AdminOnly @Operation(summary = "费用值列表")
    @GetMapping("/value/list")
    public Result<List<FeeValue>> valueList(@RequestParam Long keyId) {
        return Result.ok(feeValueService.list(
                new LambdaQueryWrapper<FeeValue>().eq(FeeValue::getFeeKeyId, keyId)));
    }

    @AdminOnly @Operation(summary = "新增/修改费用值")
    @PostMapping("/value/save")
    public Result<Void> valueSave(@RequestBody FeeValue entity) {
        feeValueService.saveOrUpdate(entity);
        return Result.ok();
    }

    @AdminOnly @Operation(summary = "删除费用值")
    @DeleteMapping("/value/{id}")
    public Result<Void> valueDelete(@PathVariable Long id) {
        feeValueService.removeById(id);
        return Result.ok();
    }
}
