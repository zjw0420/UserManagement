package com.usermgr.admin.controller.apartment;

import com.usermgr.admin.security.AdminOnly;
import com.usermgr.common.result.Result;
import com.usermgr.model.entity.PaymentType;
import com.usermgr.service.business.PaymentTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "支付方式管理")
@RestController
@RequestMapping("/admin/payment")
public class PaymentTypeController {

    @Autowired
    private PaymentTypeService paymentTypeService;

    @AdminOnly
    @Operation(summary = "支付方式列表")
    @GetMapping("/list")
    public Result<List<PaymentType>> list() {
        return Result.ok(paymentTypeService.list());
    }

    @AdminOnly
    @Operation(summary = "支付方式详情")
    @GetMapping("/{id}")
    public Result<PaymentType> getById(@PathVariable Long id) {
        return Result.ok(paymentTypeService.getById(id));
    }

    @AdminOnly
    @Operation(summary = "新增/修改支付方式")
    @PostMapping("/save")
    public Result<Void> save(@RequestBody PaymentType entity) {
        paymentTypeService.saveOrUpdate(entity);
        return Result.ok();
    }

    @AdminOnly
    @Operation(summary = "删除支付方式")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        paymentTypeService.removeById(id);
        return Result.ok();
    }
}
