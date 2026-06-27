package com.usermgr.service.business.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usermgr.model.entity.ApartmentFeeValue;
import com.usermgr.service.business.ApartmentFeeValueService;
import com.usermgr.service.mapper.ApartmentFeeValueMapper;
import org.springframework.stereotype.Service;

@Service
public class ApartmentFeeValueServiceImpl extends ServiceImpl<ApartmentFeeValueMapper, ApartmentFeeValue>
        implements ApartmentFeeValueService {
}
