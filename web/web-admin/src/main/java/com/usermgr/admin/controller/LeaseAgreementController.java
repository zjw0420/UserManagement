package com.usermgr.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usermgr.admin.security.AdminOnly;
import com.usermgr.admin.service.LeaseAgreementService;
import com.usermgr.admin.vo.agreement.AgreementQueryVo;
import com.usermgr.admin.vo.agreement.AgreementSubmitVo;
import com.usermgr.admin.vo.agreement.AgreementVo;
import com.usermgr.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "租赁合同管理")
@RestController
@RequestMapping("/admin/agreement")
public class LeaseAgreementController {

    @Autowired
    private LeaseAgreementService leaseAgreementService;

    @AdminOnly
    @Operation(summary = "分页搜索合同")
    @GetMapping("/pageItem")
    public Result<IPage<AgreementVo>> pageItem(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize,
            AgreementQueryVo queryVo) {
        Page<AgreementVo> page = new Page<>(pageNum, pageSize);
        return Result.ok(leaseAgreementService.pageItem(page, queryVo));
    }

    @AdminOnly
    @Operation(summary = "合同详情")
    @GetMapping("/getDetailById")
    public Result<AgreementVo> getDetailById(@RequestParam Long id) {
        return Result.ok(leaseAgreementService.getDetailById(id));
    }

    @AdminOnly
    @Operation(summary = "新增/修改合同")
    @PostMapping("/saveOrUpdate")
    public Result<Void> saveOrUpdate(@RequestBody AgreementSubmitVo submitVo) {
        leaseAgreementService.saveOrUpdate(submitVo);
        return Result.ok();
    }

    @AdminOnly
    @Operation(summary = "删除合同")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        leaseAgreementService.delete(id);
        return Result.ok();
    }

}
