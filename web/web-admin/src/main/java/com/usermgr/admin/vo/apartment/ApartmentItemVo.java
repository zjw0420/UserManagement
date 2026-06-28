package com.usermgr.admin.vo.apartment;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "公寓列表项")
public class ApartmentItemVo {

    @Schema(description = "公寓ID")
    private Long id;

    @Schema(description = "公寓名称")
    private String name;

    @Schema(description = "公寓介绍")
    private String introduction;

    @Schema(description = "省份名称")
    private String provinceName;

    @Schema(description = "城市名称")
    private String cityName;

    @Schema(description = "区域名称")
    private String districtName;

    @Schema(description = "详细地址")
    private String addressDetail;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "最低租金")
    private BigDecimal minRent;

    @Schema(description = "房间数量")
    private Integer roomCount;

    @Schema(description = "是否发布")
    private Integer isRelease;

}
