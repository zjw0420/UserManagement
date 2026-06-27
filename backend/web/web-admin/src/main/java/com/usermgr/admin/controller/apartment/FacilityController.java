package com.usermgr.admin.controller.apartment;

import com.usermgr.admin.security.AdminOnly;
import com.usermgr.common.result.Result;
import com.usermgr.model.entity.FacilityInfo;
import com.usermgr.service.business.FacilityInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "设施管理")
@RestController
@RequestMapping("/admin/facility")
public class FacilityController {

    @Autowired
    private FacilityInfoService facilityInfoService;

    @AdminOnly
    @Operation(summary = "设施列表")
    @GetMapping("/list")
    public Result<List<FacilityInfo>> list() {
        return Result.ok(facilityInfoService.list());
    }

    @AdminOnly
    @Operation(summary = "设施详情")
    @GetMapping("/{id}")
    public Result<FacilityInfo> getById(@PathVariable Long id) {
        return Result.ok(facilityInfoService.getById(id));
    }

    @AdminOnly
    @Operation(summary = "新增/修改设施")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody FacilityInfo entity) {
        facilityInfoService.saveOrUpdate(entity);
        return Result.ok();
    }

    @AdminOnly
    @Operation(summary = "删除设施")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        facilityInfoService.removeById(id);
        return Result.ok();
    }
}
