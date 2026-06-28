package com.usermgr.service.business.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usermgr.model.entity.AttrKey;
import com.usermgr.service.business.AttrKeyService;
import com.usermgr.service.mapper.AttrKeyMapper;
import org.springframework.stereotype.Service;

@Service
public class AttrKeyServiceImpl extends ServiceImpl<AttrKeyMapper, AttrKey> implements AttrKeyService {
}
