package com.usermgr.admin.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usermgr.admin.vo.agreement.AgreementQueryVo;
import com.usermgr.admin.vo.agreement.AgreementSubmitVo;
import com.usermgr.admin.vo.agreement.AgreementVo;

public interface LeaseAgreementService {

    IPage<AgreementVo> pageItem(Page<AgreementVo> page, AgreementQueryVo queryVo);

    AgreementVo getDetailById(Long id);

    void saveOrUpdate(AgreementSubmitVo submitVo);

    void delete(Long id);

}
