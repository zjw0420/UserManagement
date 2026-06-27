package com.usermgr.admin.vo.system;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "用户详情")
public class SystemUserInfoVo {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码(仅写入)")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Schema(description = "姓名")
    private String name;

    @Schema(description = "类型")
    private Integer type;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "头像URL")
    private String avatarUrl;

    @Schema(description = "岗位ID")
    private Long postId;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "备注")
    private String additionalInfo;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

}
