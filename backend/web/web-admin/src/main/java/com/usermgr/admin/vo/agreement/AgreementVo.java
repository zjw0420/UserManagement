package com.usermgr.admin.vo.agreement;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Schema(description = "租赁合同详情")
public class AgreementVo {

    @Schema(description = "合同ID")
    private Long id;

    @Schema(description = "承租人姓名")
    private String name;

    @Schema(description = "承租人手机号")
    private String phone;

    @Schema(description = "身份证号")
    private String identificationNumber;

    @Schema(description = "公寓ID")
    private Long apartmentId;

    @Schema(description = "房间ID")
    private Long roomId;

    @Schema(description = "房间号")
    private String roomNumber;

    @Schema(description = "租约开始日期")
    private LocalDate leaseStartDate;

    @Schema(description = "租约结束日期")
    private LocalDate leaseEndDate;

    @Schema(description = "月租金")
    private BigDecimal rent;

    @Schema(description = "押金")
    private BigDecimal deposit;

    @Schema(description = "租约状态")
    private Integer status;

    @Schema(description = "来源类型")
    private Integer sourceType;

    @Schema(description = "备注")
    private String additionalInfo;

}
