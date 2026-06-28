package com.usermgr.admin.vo.appointment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "预约详情")
public class AppointmentVo {

    @Schema(description = "预约ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "预约人姓名")
    private String name;

    @Schema(description = "预约人手机号")
    private String phone;

    @Schema(description = "公寓ID")
    private Long apartmentId;

    @Schema(description = "预约时间")
    private LocalDateTime appointmentTime;

    @Schema(description = "预约状态(1:待看房 2:已取消 3:已看房)")
    private Integer appointmentStatus;

    @Schema(description = "备注")
    private String additionalInfo;

}
