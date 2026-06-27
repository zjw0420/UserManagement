package com.usermgr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usermgr.admin.service.ApartmentInfoService;
import com.usermgr.admin.vo.apartment.*;
import com.usermgr.admin.vo.graph.GraphVo;
import com.usermgr.common.exception.BusinessException;
import com.usermgr.common.result.ResultCodeEnum;
import com.usermgr.model.entity.*;
import com.usermgr.model.enums.ItemType;
import com.usermgr.model.enums.LeaseStatus;
import com.usermgr.service.business.*;
import com.usermgr.service.mapper.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ApartmentInfoServiceImpl implements ApartmentInfoService {

    @Autowired
    private ApartmentInfoMapper apartmentInfoMapper;

    @Autowired
    private ApartmentFacilityService apartmentFacilityService;
    @Autowired
    private ApartmentLabelService apartmentLabelService;
    @Autowired
    private ApartmentFeeValueService apartmentFeeValueService;

    @Autowired
    private GraphInfoMapper graphInfoMapper;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;
    @Autowired
    private LabelInfoMapper labelInfoMapper;
    @Autowired
    private FeeValueMapper feeValueMapper;

    @Autowired
    private LeaseAgreementMapper leaseAgreementMapper;
    @Autowired
    private ViewAppointmentMapper viewAppointmentMapper;

    @Override
    @Transactional
    public void saveOrUpdate(ApartmentSubmitVo submitVo) {
        boolean isUpdate = submitVo.getId() != null;

        ApartmentInfo info = new ApartmentInfo();
        BeanUtils.copyProperties(submitVo, info);
        if (isUpdate) {
            apartmentInfoMapper.updateById(info);
        } else {
            apartmentInfoMapper.insert(info);
        }
        Long apartmentId = info.getId();

        if (isUpdate) {
            // 删除旧关联
            apartmentFacilityService.remove(
                    new LambdaQueryWrapper<ApartmentFacility>()
                            .eq(ApartmentFacility::getApartmentId, apartmentId));
            apartmentLabelService.remove(
                    new LambdaQueryWrapper<ApartmentLabel>()
                            .eq(ApartmentLabel::getApartmentId, apartmentId));
            apartmentFeeValueService.remove(
                    new LambdaQueryWrapper<ApartmentFeeValue>()
                            .eq(ApartmentFeeValue::getApartmentId, apartmentId));
            graphInfoMapper.delete(
                    new LambdaQueryWrapper<GraphInfo>()
                            .eq(GraphInfo::getItemId, apartmentId)
                            .eq(GraphInfo::getItemType, ItemType.APARTMENT.getCode()));
        }

        // 插入新关联 — 设施
        if (!CollectionUtils.isEmpty(submitVo.getFacilityInfoIds())) {
            List<ApartmentFacility> list = new ArrayList<>();
            for (Long fid : submitVo.getFacilityInfoIds()) {
                ApartmentFacility af = new ApartmentFacility();
                af.setApartmentId(apartmentId);
                af.setFacilityId(fid);
                list.add(af);
            }
            apartmentFacilityService.saveBatch(list);
        }

        // 标签
        if (!CollectionUtils.isEmpty(submitVo.getLabelIds())) {
            List<ApartmentLabel> list = new ArrayList<>();
            for (Long lid : submitVo.getLabelIds()) {
                ApartmentLabel al = new ApartmentLabel();
                al.setApartmentId(apartmentId);
                al.setLabelId(lid);
                list.add(al);
            }
            apartmentLabelService.saveBatch(list);
        }

        // 杂费
        if (!CollectionUtils.isEmpty(submitVo.getFeeValueIds())) {
            List<ApartmentFeeValue> list = new ArrayList<>();
            for (Long fid : submitVo.getFeeValueIds()) {
                ApartmentFeeValue afv = new ApartmentFeeValue();
                afv.setApartmentId(apartmentId);
                afv.setFeeValueId(fid);
                list.add(afv);
            }
            apartmentFeeValueService.saveBatch(list);
        }

        // 图片
        if (!CollectionUtils.isEmpty(submitVo.getGraphVoList())) {
            List<GraphInfo> list = new ArrayList<>();
            for (GraphVo gv : submitVo.getGraphVoList()) {
                GraphInfo gi = new GraphInfo();
                gi.setItemType(ItemType.APARTMENT);
                gi.setItemId(apartmentId);
                gi.setName(gv.getName());
                gi.setUrl(gv.getUrl());
                list.add(gi);
            }
            for (GraphInfo gi : list) {
                graphInfoMapper.insert(gi);
            }
        }
    }

    @Override
    public ApartmentDetailVo getDetailById(Long id) {
        ApartmentInfo info = apartmentInfoMapper.selectById(id);
        if (info == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND);
        }

        // 批量查关联数据 (5 次 SQL)
        List<FacilityInfo> facilities = facilityInfoMapper.selectFacilityListByApartmentId(id);
        List<LabelInfo> labels = labelInfoMapper.selectLabelListByApartmentId(id);
        List<FeeValue> fees = feeValueMapper.selectFeeValueListByApartmentId(id);
        List<GraphInfo> graphs = graphInfoMapper.selectList(
                new LambdaQueryWrapper<GraphInfo>()
                        .eq(GraphInfo::getItemId, id)
                        .eq(GraphInfo::getItemType, ItemType.APARTMENT.getCode())
        );

        ApartmentDetailVo vo = new ApartmentDetailVo();
        BeanUtils.copyProperties(info, vo);
        vo.setFacilityInfoList(facilities);
        vo.setLabelInfoList(labels);
        vo.setFeeValueList(fees);

        List<GraphVo> graphVos = graphs.stream().map(g -> {
            GraphVo gv = new GraphVo();
            gv.setName(g.getName());
            gv.setUrl(g.getUrl());
            return gv;
        }).toList();
        vo.setGraphVoList(graphVos);

        return vo;
    }

    @Override
    public IPage<ApartmentItemVo> pageItem(Page<ApartmentItemVo> page, ApartmentQueryVo queryVo) {
        LambdaQueryWrapper<ApartmentInfo> wrapper = new LambdaQueryWrapper<>();
        if (queryVo != null) {
            if (queryVo.getProvinceId() != null) wrapper.eq(ApartmentInfo::getProvinceId, queryVo.getProvinceId());
            if (queryVo.getCityId() != null) wrapper.eq(ApartmentInfo::getCityId, queryVo.getCityId());
            if (queryVo.getDistrictId() != null) wrapper.eq(ApartmentInfo::getDistrictId, queryVo.getDistrictId());
            if (queryVo.getName() != null && !queryVo.getName().isBlank())
                wrapper.like(ApartmentInfo::getName, queryVo.getName());
            if (queryVo.getIsRelease() != null) wrapper.eq(ApartmentInfo::getIsRelease, queryVo.getIsRelease());
        }
        wrapper.orderByDesc(ApartmentInfo::getCreateTime);

        Page<ApartmentInfo> entityPage = new Page<>(page.getCurrent(), page.getSize());
        IPage<ApartmentInfo> result = apartmentInfoMapper.selectPage(entityPage, wrapper);

        List<ApartmentItemVo> vos = result.getRecords().stream().map(a -> {
            ApartmentItemVo vo = new ApartmentItemVo();
            BeanUtils.copyProperties(a, vo);
            return vo;
        }).toList();

        IPage<ApartmentItemVo> voPage = new Page<>(page.getCurrent(), page.getSize(), result.getTotal());
        voPage.setRecords(vos);
        return voPage;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 级联校验: 查活跃租约
        Long leaseCount = leaseAgreementMapper.selectCount(
                new LambdaQueryWrapper<LeaseAgreement>()
                        .eq(LeaseAgreement::getApartmentId, id)
                        .in(LeaseAgreement::getStatus, LeaseStatus.SIGNED.getCode())
        );
        if (leaseCount > 0) {
            throw new BusinessException(ResultCodeEnum.HAS_ACTIVE_LEASE);
        }

        // 级联校验: 查未完成预约
        Long appointmentCount = viewAppointmentMapper.selectCount(
                new LambdaQueryWrapper<ViewAppointment>()
                        .eq(ViewAppointment::getApartmentId, id)
                        .eq(ViewAppointment::getAppointmentStatus, 1) // 待看房
        );
        if (appointmentCount > 0) {
            throw new BusinessException(ResultCodeEnum.HAS_ACTIVE_APPOINTMENT);
        }

        apartmentInfoMapper.deleteById(id);
    }

}
