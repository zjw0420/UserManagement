package com.usermgr.service.business.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usermgr.model.entity.FeeValue;
import com.usermgr.service.business.FeeValueService;
import com.usermgr.service.mapper.FeeValueMapper;
import org.springframework.stereotype.Service;

@Service
public class FeeValueServiceImpl extends ServiceImpl<FeeValueMapper, FeeValue> implements FeeValueService {
}
