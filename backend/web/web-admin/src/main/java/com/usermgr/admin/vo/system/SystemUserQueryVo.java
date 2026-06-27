package com.usermgr.admin.vo.system;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户搜索条件")
public class SystemUserQueryVo {

    @Schema(description = "用户名/姓名模糊搜索")
    private String keyword;

    @Schema(description = "用户类型")
    private Integer type;

    @Schema(description = "状态")
    private Integer status;

}
