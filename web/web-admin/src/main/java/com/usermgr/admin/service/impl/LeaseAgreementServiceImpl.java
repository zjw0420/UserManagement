package com.usermgr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usermgr.admin.service.LeaseAgreementService;
import com.usermgr.admin.vo.agreement.AgreementQueryVo;
import com.usermgr.admin.vo.agreement.AgreementSubmitVo;
import com.usermgr.admin.vo.agreement.AgreementVo;
import com.usermgr.common.exception.BusinessException;
import com.usermgr.common.result.ResultCodeEnum;
import com.usermgr.model.entity.LeaseAgreement;
import com.usermgr.service.mapper.LeaseAgreementMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaseAgreementServiceImpl implements LeaseAgreementService {

    @Autowired
    private LeaseAgreementMapper leaseAgreementMapper;

    @Override
    public IPage<AgreementVo> pageItem(Page<AgreementVo> page, AgreementQueryVo queryVo) {
        LambdaQueryWrapper<LeaseAgreement> wrapper = new LambdaQueryWrapper<>();
        if (queryVo != null) {
            if (queryVo.getApartmentId() != null) wrapper.eq(LeaseAgreement::getApartmentId, queryVo.getApartmentId());
            if (queryVo.getRoomId() != null) wrapper.eq(LeaseAgreement::getRoomId, queryVo.getRoomId());
            if (queryVo.getName() != null && !queryVo.getName().isBlank())
                wrapper.like(LeaseAgreement::getName, queryVo.getName());
            if (queryVo.getPhone() != null && !queryVo.getPhone().isBlank())
                wrapper.like(LeaseAgreement::getPhone, queryVo.getPhone());
            if (queryVo.getStatus() != null) wrapper.eq(LeaseAgreement::getStatus, queryVo.getStatus());
        }
        wrapper.orderByDesc(LeaseAgreement::getCreateTime);

        Page<LeaseAgreement> entityPage = new Page<>(page.getCurrent(), page.getSize());
        IPage<LeaseAgreement> result = leaseAgreementMapper.selectPage(entityPage, wrapper);

        List<AgreementVo> vos = result.getRecords().stream().map(a -> {
            AgreementVo vo = new AgreementVo();
            BeanUtils.copyProperties(a, vo);
            return vo;
        }).toList();

        IPage<AgreementVo> voPage = new Page<>(page.getCurrent(), page.getSize(), result.getTotal());
        voPage.setRecords(vos);
        return voPage;
    }

    @Override
    public AgreementVo getDetailById(Long id) {
        LeaseAgreement entity = leaseAgreementMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND);
        }
        AgreementVo vo = new AgreementVo();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }

    @Override
    public void saveOrUpdate(AgreementSubmitVo submitVo) {
        LeaseAgreement entity;
        if (submitVo.getId() != null) {
            entity = leaseAgreementMapper.selectById(submitVo.getId());
            if (entity == null) {
                throw new BusinessException(ResultCodeEnum.NOT_FOUND);
            }
        } else {
            entity = new LeaseAgreement();
        }
        BeanUtils.copyProperties(submitVo, entity);
        // 日期转换: LocalDate -> Date
        if (submitVo.getLeaseStartDate() != null) {
            entity.setLeaseStartDate(java.sql.Date.valueOf(submitVo.getLeaseStartDate()));
        }
        if (submitVo.getLeaseEndDate() != null) {
            entity.setLeaseEndDate(java.sql.Date.valueOf(submitVo.getLeaseEndDate()));
        }

        if (submitVo.getId() != null) {
            leaseAgreementMapper.updateById(entity);
        } else {
            leaseAgreementMapper.insert(entity);
        }
    }

    @Override
    public void delete(Long id) {
        LeaseAgreement entity = leaseAgreementMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND);
        }
        leaseAgreementMapper.deleteById(id);
    }

}
