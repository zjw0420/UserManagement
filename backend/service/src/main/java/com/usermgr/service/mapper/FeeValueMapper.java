package com.usermgr.service.mapper;

import com.usermgr.model.entity.FeeValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FeeValueMapper extends BaseMapper<FeeValue> {

    @Select("SELECT fv.* FROM fee_value fv "
            + "JOIN apartment_fee_value afv ON fv.id = afv.fee_value_id "
            + "WHERE afv.is_deleted = 0 AND fv.is_deleted = 0 "
            + "AND afv.apartment_id = #{id}")
    List<FeeValue> selectFeeValueListByApartmentId(Long id);

}
