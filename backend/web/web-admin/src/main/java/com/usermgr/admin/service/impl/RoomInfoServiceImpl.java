package com.usermgr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usermgr.admin.service.RoomInfoService;
import com.usermgr.admin.vo.room.*;
import com.usermgr.common.exception.BusinessException;
import com.usermgr.common.result.ResultCodeEnum;
import com.usermgr.model.entity.*;
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
public class RoomInfoServiceImpl implements RoomInfoService {

    @Autowired
    private RoomInfoMapper roomInfoMapper;

    @Autowired
    private RoomFacilityService roomFacilityService;
    @Autowired
    private RoomLabelService roomLabelService;
    @Autowired
    private RoomAttrValueService roomAttrValueService;
    @Autowired
    private RoomLeaseTermService roomLeaseTermService;
    @Autowired
    private RoomPaymentTypeService roomPaymentTypeService;

    @Autowired
    private FacilityInfoMapper facilityInfoMapper;
    @Autowired
    private LabelInfoMapper labelInfoMapper;
    @Autowired
    private AttrValueMapper attrValueMapper;
    @Autowired
    private LeaseTermMapper leaseTermMapper;
    @Autowired
    private PaymentTypeMapper paymentTypeMapper;

    @Autowired
    private LeaseAgreementMapper leaseAgreementMapper;

    @Override
    @Transactional
    public void saveOrUpdate(RoomSubmitVo submitVo) {
        boolean isUpdate = submitVo.getId() != null;

        RoomInfo info = new RoomInfo();
        BeanUtils.copyProperties(submitVo, info);
        if (isUpdate) {
            roomInfoMapper.updateById(info);
        } else {
            roomInfoMapper.insert(info);
        }
        Long roomId = info.getId();

        if (isUpdate) {
            roomFacilityService.remove(
                    new LambdaQueryWrapper<RoomFacility>().eq(RoomFacility::getRoomId, roomId));
            roomLabelService.remove(
                    new LambdaQueryWrapper<RoomLabel>().eq(RoomLabel::getRoomId, roomId));
            roomAttrValueService.remove(
                    new LambdaQueryWrapper<RoomAttrValue>().eq(RoomAttrValue::getRoomId, roomId));
            roomLeaseTermService.remove(
                    new LambdaQueryWrapper<RoomLeaseTerm>().eq(RoomLeaseTerm::getRoomId, roomId));
            roomPaymentTypeService.remove(
                    new LambdaQueryWrapper<RoomPaymentType>().eq(RoomPaymentType::getRoomId, roomId));
        }

        // 设施
        if (!CollectionUtils.isEmpty(submitVo.getFacilityInfoIds())) {
            List<RoomFacility> list = new ArrayList<>();
            for (Long fid : submitVo.getFacilityInfoIds()) {
                RoomFacility rf = new RoomFacility();
                rf.setRoomId(roomId);
                rf.setFacilityId(fid);
                list.add(rf);
            }
            roomFacilityService.saveBatch(list);
        }

        // 标签
        if (!CollectionUtils.isEmpty(submitVo.getLabelIds())) {
            List<RoomLabel> list = new ArrayList<>();
            for (Long lid : submitVo.getLabelIds()) {
                RoomLabel rl = new RoomLabel();
                rl.setRoomId(roomId);
                rl.setLabelId(lid);
                list.add(rl);
            }
            roomLabelService.saveBatch(list);
        }

        // 属性值
        if (!CollectionUtils.isEmpty(submitVo.getAttrValueIds())) {
            List<RoomAttrValue> list = new ArrayList<>();
            for (Long aid : submitVo.getAttrValueIds()) {
                RoomAttrValue rav = new RoomAttrValue();
                rav.setRoomId(roomId);
                rav.setAttrValueId(aid);
                list.add(rav);
            }
            roomAttrValueService.saveBatch(list);
        }

        // 租期
        if (!CollectionUtils.isEmpty(submitVo.getLeaseTermIds())) {
            List<RoomLeaseTerm> list = new ArrayList<>();
            for (Long lid : submitVo.getLeaseTermIds()) {
                RoomLeaseTerm rlt = new RoomLeaseTerm();
                rlt.setRoomId(roomId);
                rlt.setLeaseTermId(lid);
                list.add(rlt);
            }
            roomLeaseTermService.saveBatch(list);
        }

        // 支付方式
        if (!CollectionUtils.isEmpty(submitVo.getPaymentTypeIds())) {
            List<RoomPaymentType> list = new ArrayList<>();
            for (Long pid : submitVo.getPaymentTypeIds()) {
                RoomPaymentType rpt = new RoomPaymentType();
                rpt.setRoomId(roomId);
                rpt.setPaymentTypeId(pid);
                list.add(rpt);
            }
            roomPaymentTypeService.saveBatch(list);
        }
    }

    @Override
    public RoomDetailVo getDetailById(Long id) {
        RoomInfo info = roomInfoMapper.selectById(id);
        if (info == null) {
            throw new BusinessException(ResultCodeEnum.NOT_FOUND);
        }

        List<FacilityInfo> facilities = facilityInfoMapper.selectFacilityListByRoomId(id);
        List<LabelInfo> labels = labelInfoMapper.selectLabelListByRoomId(id);
        List<AttrValue> attrValues = attrValueMapper.selectAttrValueListByRoomId(id);
        List<LeaseTerm> leaseTerms = leaseTermMapper.selectLeaseTermListByRoomId(id);
        List<PaymentType> paymentTypes = paymentTypeMapper.selectPaymentTypeListByRoomId(id);

        RoomDetailVo vo = new RoomDetailVo();
        BeanUtils.copyProperties(info, vo);
        vo.setFacilityInfoList(facilities);
        vo.setLabelInfoList(labels);
        vo.setAttrValueList(attrValues);
        vo.setLeaseTermList(leaseTerms);
        vo.setPaymentTypeList(paymentTypes);

        return vo;
    }

    @Override
    public IPage<RoomItemVo> pageItem(Page<RoomItemVo> page, RoomQueryVo queryVo) {
        LambdaQueryWrapper<RoomInfo> wrapper = new LambdaQueryWrapper<>();
        if (queryVo != null) {
            if (queryVo.getApartmentId() != null) wrapper.eq(RoomInfo::getApartmentId, queryVo.getApartmentId());
            if (queryVo.getRoomNumber() != null && !queryVo.getRoomNumber().isBlank())
                wrapper.like(RoomInfo::getRoomNumber, queryVo.getRoomNumber());
            if (queryVo.getIsRelease() != null) wrapper.eq(RoomInfo::getIsRelease, queryVo.getIsRelease());
        }
        wrapper.orderByAsc(RoomInfo::getRoomNumber);

        Page<RoomInfo> entityPage = new Page<>(page.getCurrent(), page.getSize());
        IPage<RoomInfo> result = roomInfoMapper.selectPage(entityPage, wrapper);

        List<RoomItemVo> vos = result.getRecords().stream().map(r -> {
            RoomItemVo vo = new RoomItemVo();
            BeanUtils.copyProperties(r, vo);
            return vo;
        }).toList();

        IPage<RoomItemVo> voPage = new Page<>(page.getCurrent(), page.getSize(), result.getTotal());
        voPage.setRecords(vos);
        return voPage;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Long leaseCount = leaseAgreementMapper.selectCount(
                new LambdaQueryWrapper<LeaseAgreement>()
                        .eq(LeaseAgreement::getRoomId, id)
                        .in(LeaseAgreement::getStatus, LeaseStatus.SIGNED.getCode())
        );
        if (leaseCount > 0) {
            throw new BusinessException(ResultCodeEnum.HAS_ACTIVE_LEASE);
        }

        roomInfoMapper.deleteById(id);
    }

}
