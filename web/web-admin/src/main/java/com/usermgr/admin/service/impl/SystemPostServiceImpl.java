package com.usermgr.admin.service.impl;

import com.usermgr.admin.service.SystemPostService;
import com.usermgr.model.entity.SystemPost;
import com.usermgr.service.mapper.SystemPostMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SystemPostServiceImpl implements SystemPostService {

    @Autowired
    private SystemPostMapper systemPostMapper;

    @Override
    public List<SystemPost> listAll() {
        return systemPostMapper.selectList(null);
    }

    @Override
    public void save(SystemPost post) {
        systemPostMapper.insert(post);
    }

    @Override
    public void delete(Long id) {
        systemPostMapper.deleteById(id);
    }

}
