package com.usermgr.admin.vo.room;

import com.usermgr.model.entity.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "房间详情")
public class RoomDetailVo {

    @Schema(description = "房间ID")
    private Long id;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "月租金")
    private BigDecimal rent;

    @Schema(description = "所属公寓ID")
    private Long apartmentId;

    @Schema(description = "是否发布")
    private Integer isRelease;

    @Schema(description = "配套设施")
    private List<FacilityInfo> facilityInfoList;

    @Schema(description = "标签")
    private List<LabelInfo> labelInfoList;

    @Schema(description = "属性值")
    private List<AttrValue> attrValueList;

    @Schema(description = "租期")
    private List<LeaseTerm> leaseTermList;

    @Schema(description = "支付方式")
    private List<PaymentType> paymentTypeList;

}
