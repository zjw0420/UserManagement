package com.usermgr.service.business.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usermgr.model.entity.FeeKey;
import com.usermgr.service.business.FeeKeyService;
import com.usermgr.service.mapper.FeeKeyMapper;
import org.springframework.stereotype.Service;

@Service
public class FeeKeyServiceImpl extends ServiceImpl<FeeKeyMapper, FeeKey> implements FeeKeyService {
}
