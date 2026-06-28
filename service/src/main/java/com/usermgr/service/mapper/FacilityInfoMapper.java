package com.usermgr.service.mapper;

import com.usermgr.model.entity.FacilityInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface FacilityInfoMapper extends BaseMapper<FacilityInfo> {

    @Select("SELECT * FROM facility_info WHERE is_deleted = 0 "
            + "AND id IN (SELECT facility_id FROM apartment_facility "
            + "WHERE is_deleted = 0 AND apartment_id = #{id})")
    List<FacilityInfo> selectFacilityListByApartmentId(Long id);

    @Select("SELECT * FROM facility_info WHERE is_deleted = 0 "
            + "AND id IN (SELECT facility_id FROM room_facility "
            + "WHERE is_deleted = 0 AND room_id = #{id})")
    List<FacilityInfo> selectFacilityListByRoomId(Long id);

}
