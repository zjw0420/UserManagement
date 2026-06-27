package com.usermgr.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usermgr.admin.vo.room.*;

public interface RoomInfoService {

    void saveOrUpdate(RoomSubmitVo submitVo);

    RoomDetailVo getDetailById(Long id);

    IPage<RoomItemVo> pageItem(Page<RoomItemVo> page, RoomQueryVo queryVo);

    void delete(Long id);

}
