package com.usermgr.admin.controller.apartment;

import com.usermgr.admin.security.AdminOnly;
import com.usermgr.common.result.Result;
import com.usermgr.model.entity.LeaseTerm;
import com.usermgr.service.business.LeaseTermService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "租期管理")
@RestController
@RequestMapping("/admin/lease-term")
public class LeaseTermController {

    @Autowired
    private LeaseTermService leaseTermService;

    @AdminOnly
    @Operation(summary = "租期列表")
    @GetMapping("/list")
    public Result<List<LeaseTerm>> list() {
        return Result.ok(leaseTermService.list());
    }

    @AdminOnly
    @Operation(summary = "租期详情")
    @GetMapping("/{id}")
    public Result<LeaseTerm> getById(@PathVariable Long id) {
        return Result.ok(leaseTermService.getById(id));
    }

    @AdminOnly
    @Operation(summary = "新增/修改租期")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody LeaseTerm entity) {
        leaseTermService.saveOrUpdate(entity);
        return Result.ok();
    }

    @AdminOnly
    @Operation(summary = "删除租期")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        leaseTermService.removeById(id);
        return Result.ok();
    }
}
