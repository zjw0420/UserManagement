package com.usermgr.admin.vo.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "房间列表项")
public class RoomItemVo {

    @Schema(description = "房间ID")
    private Long id;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "月租金")
    private BigDecimal rent;

    @Schema(description = "所属公寓ID")
    private Long apartmentId;

    @Schema(description = "公寓名称")
    private String apartmentName;

    @Schema(description = "是否发布")
    private Integer isRelease;

}
