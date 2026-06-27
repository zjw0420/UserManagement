package com.usermgr.service.mapper;

import com.usermgr.model.entity.PaymentType;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PaymentTypeMapper extends BaseMapper<PaymentType> {

    @Select("SELECT pt.* FROM payment_type pt "
            + "JOIN room_payment_type rpt ON pt.id = rpt.payment_type_id "
            + "WHERE rpt.is_deleted = 0 AND pt.is_deleted = 0 "
            + "AND rpt.room_id = #{id}")
    List<PaymentType> selectPaymentTypeListByRoomId(Long id);

}
