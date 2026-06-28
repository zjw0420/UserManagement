package com.usermgr.admin.vo.apartment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "公寓搜索条件")
public class ApartmentQueryVo {

    @Schema(description = "省份ID")
    private Long provinceId;

    @Schema(description = "城市ID")
    private Long cityId;

    @Schema(description = "区域ID")
    private Long districtId;

    @Schema(description = "公寓名称")
    private String name;

    @Schema(description = "发布状态(1:已发布 0:未发布)")
    private Integer isRelease;

}
