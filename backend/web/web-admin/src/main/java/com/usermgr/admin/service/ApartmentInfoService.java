package com.usermgr.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usermgr.admin.vo.apartment.*;

public interface ApartmentInfoService {

    void saveOrUpdate(ApartmentSubmitVo submitVo);

    ApartmentDetailVo getDetailById(Long id);

    IPage<ApartmentItemVo> pageItem(Page<ApartmentItemVo> page, ApartmentQueryVo queryVo);

    void delete(Long id);

}
