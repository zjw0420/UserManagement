package com.usermgr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usermgr.admin.service.ViewAppointmentService;
import com.usermgr.admin.vo.appointment.AppointmentQueryVo;
import com.usermgr.admin.vo.appointment.AppointmentVo;
import com.usermgr.common.exception.BusinessException;
import com.usermgr.common.result.ResultCodeEnum;
import com.usermgr.model.entity.ViewAppointment;
import com.usermgr.model.enums.AppointmentStatus;
import com.usermgr.service.mapper.ViewAppointmentMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ViewAppointmentServiceImpl implements ViewAppointmentService {

    @Autowired
    private ViewAppointmentMapper viewAppointmentMapper;

    @Override
    public IPage<AppointmentVo> pageItem(Page<AppointmentVo> page, AppointmentQueryVo queryVo) {
        LambdaQueryWrapper<ViewAppointment> wrapper = new LambdaQueryWrapper<>();
        if (queryVo != null) {
            if (queryVo.getApartmentId() != null) wrapper.eq(ViewAppointment::getApartmentId, queryVo.getApartmentId());
            if (queryVo.getName() != null && !queryVo.getName().isBlank())
                wrapper.like(ViewAppointment::getName, queryVo.getName());
            if (queryVo.getAppointmentStatus() != null)
                wrapper.eq(ViewAppointment::getAppointmentStatus, queryVo.getAppointmentStatus());
        }
        wrapper.orderByDesc(ViewAppointment::getCreateTime);

        Page<ViewAppointment> entityPage = new Page<>(page.getCurrent(), page.getSize());
        IPage<ViewAppointment> result = viewAppointmentMapper.selectPage(entityPage, wrapper);

        List<AppointmentVo> vos = result.getRecords().stream().map(a -> {
            AppointmentVo vo = new AppointmentVo();
            BeanUtils.copyProperties(a, vo);
            return vo;
        }).toList();

        IPage<AppointmentVo> voPage = new Page<>(page.getCurrent(), page.getSize(), result.getTotal());
        voPage.setRecords(vos);
        return voPage;
    }

    @Override
    public AppointmentVo getDetailById(Long id) {
        ViewAppointment entity = viewAppointmentMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND);
        }
        AppointmentVo vo = new AppointmentVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        ViewAppointment entity = viewAppointmentMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND);
        }
        // Integer → 枚举转换
        AppointmentStatus newStatus = switch (status) {
            case 2 -> AppointmentStatus.CANCELED;
            case 3 -> AppointmentStatus.VIEWED;
            default -> AppointmentStatus.WAITING;
        };
        entity.setAppointmentStatus(newStatus);
        viewAppointmentMapper.updateById(entity);
    }

    @Override
    public void delete(Long id) {
        ViewAppointment entity = viewAppointmentMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND);
        }
        viewAppointmentMapper.deleteById(id);
    }

}
