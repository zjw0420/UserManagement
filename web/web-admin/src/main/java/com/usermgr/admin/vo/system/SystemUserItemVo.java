package com.usermgr.admin.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "用户列表项")
public class SystemUserItemVo {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "类型(0:GENERAL 1:ADMIN)")
    private Integer type;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "岗位名称")
    private String postName;

    @Schema(description = "状态(1:正常 0:禁用)")
    private Integer status;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

}
