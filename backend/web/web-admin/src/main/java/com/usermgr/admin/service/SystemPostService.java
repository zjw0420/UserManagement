package com.usermgr.admin.service;

import com.usermgr.model.entity.SystemPost;

import java.util.List;

public interface SystemPostService {

    List<SystemPost> listAll();

    void save(SystemPost post);

    void delete(Long id);

}
