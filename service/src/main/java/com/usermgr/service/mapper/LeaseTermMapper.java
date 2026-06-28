package com.usermgr.service.mapper;

import com.usermgr.model.entity.LeaseTerm;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LeaseTermMapper extends BaseMapper<LeaseTerm> {

    @Select("SELECT lt.* FROM lease_term lt "
            + "JOIN room_lease_term rlt ON lt.id = rlt.lease_term_id "
            + "WHERE rlt.is_deleted = 0 AND lt.is_deleted = 0 "
            + "AND rlt.room_id = #{id}")
    List<LeaseTerm> selectLeaseTermListByRoomId(Long id);

}
