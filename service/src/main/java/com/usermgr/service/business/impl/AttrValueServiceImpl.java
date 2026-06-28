package com.usermgr.service.business.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.usermgr.model.entity.AttrValue;
import com.usermgr.service.business.AttrValueService;
import com.usermgr.service.mapper.AttrValueMapper;
import org.springframework.stereotype.Service;

@Service
public class AttrValueServiceImpl extends ServiceImpl<AttrValueMapper, AttrValue> implements AttrValueService {
}
