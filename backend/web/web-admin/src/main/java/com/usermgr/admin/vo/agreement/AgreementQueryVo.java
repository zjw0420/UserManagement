package com.usermgr.admin.vo.agreement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "租赁合同搜索条件")
public class AgreementQueryVo {

    @Schema(description = "公寓ID")
    private Long apartmentId;

    @Schema(description = "房间ID")
    private Long roomId;

    @Schema(description = "承租人姓名")
    private String name;

    @Schema(description = "承租人手机号")
    private String phone;

    @Schema(description = "租约状态")
    private Integer status;

}
