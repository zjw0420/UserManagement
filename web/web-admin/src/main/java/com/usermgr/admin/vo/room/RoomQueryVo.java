package com.usermgr.admin.vo.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "房间搜索条件")
public class RoomQueryVo {

    @Schema(description = "所属公寓ID")
    private Long apartmentId;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "发布状态")
    private Integer isRelease;

}
