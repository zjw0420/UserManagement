package com.usermgr.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usermgr.admin.vo.appointment.AppointmentQueryVo;
import com.usermgr.admin.vo.appointment.AppointmentVo;

public interface ViewAppointmentService {

    IPage<AppointmentVo> pageItem(Page<AppointmentVo> page, AppointmentQueryVo queryVo);

    AppointmentVo getDetailById(Long id);

    void updateStatus(Long id, Integer status);

    void delete(Long id);

}
