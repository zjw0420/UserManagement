package com.usermgr.admin.controller.apartment;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usermgr.admin.security.AdminOnly;
import com.usermgr.admin.service.RoomInfoService;
import com.usermgr.admin.vo.room.*;
import com.usermgr.common.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Tag(name = "房间管理")
@RestController
@RequestMapping("/admin/room")
public class RoomController {

    @Autowired
    private RoomInfoService roomInfoService;

    @AdminOnly
    @Operation(summary = "分页搜索房间")
    @GetMapping("/pageItem")
    public Result<IPage<RoomItemVo>> pageItem(
            @RequestParam(defaultValue = "1") long pageNum,
            @RequestParam(defaultValue = "10") long pageSize,
            RoomQueryVo queryVo) {
        Page<RoomItemVo> page = new Page<>(pageNum, pageSize);
        return Result.ok(roomInfoService.pageItem(page, queryVo));
    }

    @AdminOnly
    @Operation(summary = "房间详情")
    @GetMapping("/getDetailById")
    public Result<RoomDetailVo> getDetailById(@RequestParam Long id) {
        return Result.ok(roomInfoService.getDetailById(id));
    }

    @AdminOnly
    @Operation(summary = "新增/修改房间")
    @PostMapping("/saveOrUpdate")
    public Result<Void> saveOrUpdate(@RequestBody RoomSubmitVo submitVo) {
        roomInfoService.saveOrUpdate(submitVo);
        return Result.ok();
    }

    @AdminOnly
    @Operation(summary = "删除房间")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        roomInfoService.delete(id);
        return Result.ok();
    }

}
