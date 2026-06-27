package com.usermgr.service.mapper;

import com.usermgr.model.entity.LabelInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LabelInfoMapper extends BaseMapper<LabelInfo> {

    @Select("SELECT * FROM label_info WHERE is_deleted = 0 "
            + "AND id IN (SELECT label_id FROM apartment_label "
            + "WHERE is_deleted = 0 AND apartment_id = #{id})")
    List<LabelInfo> selectLabelListByApartmentId(Long id);

    @Select("SELECT * FROM label_info WHERE is_deleted = 0 "
            + "AND id IN (SELECT label_id FROM room_label "
            + "WHERE is_deleted = 0 AND room_id = #{id})")
    List<LabelInfo> selectLabelListByRoomId(Long id);

}
