package com.usermgr.admin.vo.appointment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "预约搜索条件")
public class AppointmentQueryVo {

    @Schema(description = "公寓ID")
    private Long apartmentId;

    @Schema(description = "预约人姓名")
    private String name;

    @Schema(description = "预约状态")
    private Integer appointmentStatus;

}
