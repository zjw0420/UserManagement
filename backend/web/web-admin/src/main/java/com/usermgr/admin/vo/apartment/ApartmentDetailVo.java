package com.usermgr.admin.vo.apartment;

import com.usermgr.admin.vo.graph.GraphVo;
import com.usermgr.model.entity.FacilityInfo;
import com.usermgr.model.entity.FeeValue;
import com.usermgr.model.entity.LabelInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "公寓详情")
public class ApartmentDetailVo {

    @Schema(description = "公寓ID")
    private Long id;

    @Schema(description = "公寓名称")
    private String name;

    @Schema(description = "公寓介绍")
    private String introduction;

    @Schema(description = "省份ID")
    private Long provinceId;

    @Schema(description = "省份名称")
    private String provinceName;

    @Schema(description = "城市ID")
    private Long cityId;

    @Schema(description = "城市名称")
    private String cityName;

    @Schema(description = "区域ID")
    private Long districtId;

    @Schema(description = "区域名称")
    private String districtName;

    @Schema(description = "详细地址")
    private String addressDetail;

    @Schema(description = "经度")
    private String latitude;

    @Schema(description = "纬度")
    private String longitude;

    @Schema(description = "联系电话")
    private String phone;

    @Schema(description = "是否发布")
    private Integer isRelease;

    @Schema(description = "配套设施")
    private List<FacilityInfo> facilityInfoList;

    @Schema(description = "标签")
    private List<LabelInfo> labelInfoList;

    @Schema(description = "杂费")
    private List<FeeValue> feeValueList;

    @Schema(description = "图片")
    private List<GraphVo> graphVoList;

}
