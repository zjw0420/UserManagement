package com.usermgr.admin.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usermgr.admin.security.AdminOnly;
import com.usermgr.admin.service.ViewAppointmentService;
import com.usermgr.admin.vo.appointment.AppointmentQueryVo;
import com.usermgr.admin.vo.appointment.AppointmentVo;
import com.usermgr.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "预约看房管理")
@RestController
@RequestMapping("/admin/appointment")
public class ViewAppointmentController {

    @Autowired
    private ViewAppointmentService viewAppointmentService;

    @AdminOnly
    @Operation(summary = "分页搜索预约")
    @GetMapping("/pageItem")
    public Result<IPage<AppointmentVo>> pageItem(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize,
            AppointmentQueryVo queryVo) {
        Page<AppointmentVo> page = new Page<>(pageNum, pageSize);
        return Result.ok(viewAppointmentService.pageItem(page, queryVo));
    }

    @AdminOnly
    @Operation(summary = "预约详情")
    @GetMapping("/getDetailById")
    public Result<AppointmentVo> getDetailById(@RequestParam Long id) {
        return Result.ok(viewAppointmentService.getDetailById(id));
    }

    @AdminOnly
    @Operation(summary = "更新预约状态")
    @PutMapping("/updateStatus")
    public Result<Void> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        viewAppointmentService.updateStatus(id, status);
        return Result.ok();
    }

    @AdminOnly
    @Operation(summary = "删除预约")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        viewAppointmentService.delete(id);
        return Result.ok();
    }

}
