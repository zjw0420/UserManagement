package com.usermgr.admin.vo.room;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "房间提交表单")
public class RoomSubmitVo {

    @Schema(description = "房间ID(修改时传)")
    private Long id;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "月租金")
    private BigDecimal rent;

    @Schema(description = "所属公寓ID")
    private Long apartmentId;

    @Schema(description = "是否发布")
    private Integer isRelease;

    @Schema(description = "设施ID列表")
    private List<Long> facilityInfoIds;

    @Schema(description = "标签ID列表")
    private List<Long> labelIds;

    @Schema(description = "属性值ID列表")
    private List<Long> attrValueIds;

    @Schema(description = "租期ID列表")
    private List<Long> leaseTermIds;

    @Schema(description = "支付方式ID列表")
    private List<Long> paymentTypeIds;

}
