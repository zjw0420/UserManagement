package com.usermgr.service.mapper;

import com.usermgr.model.entity.AttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AttrValueMapper extends BaseMapper<AttrValue> {

    @Select("SELECT av.* FROM attr_value av "
            + "JOIN room_attr_value rav ON av.id = rav.attr_value_id "
            + "WHERE rav.is_deleted = 0 AND av.is_deleted = 0 "
            + "AND rav.room_id = #{id}")
    List<AttrValue> selectAttrValueListByRoomId(Long id);

}
