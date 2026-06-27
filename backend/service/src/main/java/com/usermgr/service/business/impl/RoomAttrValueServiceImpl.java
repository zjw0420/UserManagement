package com.usermgr.service.business.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usermgr.model.entity.RoomAttrValue;
import com.usermgr.service.business.RoomAttrValueService;
import com.usermgr.service.mapper.RoomAttrValueMapper;
import org.springframework.stereotype.Service;

@Service
public class RoomAttrValueServiceImpl extends ServiceImpl<RoomAttrValueMapper, RoomAttrValue>
        implements RoomAttrValueService {
}
