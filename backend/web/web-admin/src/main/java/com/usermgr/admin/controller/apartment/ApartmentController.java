package com.usermgr.admin.controller.apartment;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usermgr.admin.security.AdminOnly;
import com.usermgr.admin.service.ApartmentInfoService;
import com.usermgr.admin.vo.apartment.*;
import com.usermgr.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "公寓管理")
@RestController
@RequestMapping("/admin/apartment")
public class ApartmentController {

    @Autowired
    private ApartmentInfoService apartmentInfoService;

    @AdminOnly
    @Operation(summary = "分页搜索公寓")
    @GetMapping("/pageItem")
    public Result<IPage<ApartmentItemVo>> pageItem(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize,
            ApartmentQueryVo queryVo) {
        Page<ApartmentItemVo> page = new Page<>(pageNum, pageSize);
        return Result.ok(apartmentInfoService.pageItem(page, queryVo));
    }

    @AdminOnly
    @Operation(summary = "公寓详情")
    @GetMapping("/getDetailById")
    public Result<ApartmentDetailVo> getDetailById(@RequestParam Long id) {
        return Result.ok(apartmentInfoService.getDetailById(id));
    }

    @AdminOnly
    @Operation(summary = "新增/修改公寓")
    @PostMapping("/saveOrUpdate")
    public Result<Void> saveOrUpdate(@RequestBody ApartmentSubmitVo submitVo) {
        apartmentInfoService.saveOrUpdate(submitVo);
        return Result.ok();
    }

    @AdminOnly
    @Operation(summary = "删除公寓")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        apartmentInfoService.delete(id);
        return Result.ok();
    }

}
