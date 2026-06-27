package com.usermgr.admin.vo.apartment;

import com.usermgr.admin.vo.graph.GraphVo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "公寓提交表单")
public class ApartmentSubmitVo {

    @Schema(description = "公寓ID(修改时传)")
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

    @Schema(description = "设施ID列表")
    private List<Long> facilityInfoIds;

    @Schema(description = "标签ID列表")
    private List<Long> labelIds;

    @Schema(description = "杂费值ID列表")
    private List<Long> feeValueIds;

    @Schema(description = "图片列表")
    private List<GraphVo> graphVoList;

}
