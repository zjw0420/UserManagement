package com.usermgr.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.usermgr.admin.security.JwtTokenService;
import com.usermgr.admin.service.SystemUserService;
import com.usermgr.admin.vo.system.SystemUserInfoVo;
import com.usermgr.admin.vo.system.SystemUserItemVo;
import com.usermgr.admin.vo.system.SystemUserQueryVo;
import com.usermgr.common.exception.BusinessException;
import com.usermgr.common.result.ResultCodeEnum;
import com.usermgr.model.entity.SystemPost;
import com.usermgr.model.entity.SystemUser;
import com.usermgr.service.mapper.SystemPostMapper;
import com.usermgr.service.mapper.SystemUserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SystemUserServiceImpl implements SystemUserService {

    @Autowired
    private SystemUserMapper systemUserMapper;

    @Autowired
    private SystemPostMapper systemPostMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenService jwtTokenService;

    @Override
    public IPage<SystemUserItemVo> pageUsers(Page<SystemUserItemVo> page, SystemUserQueryVo queryVo) {
        LambdaQueryWrapper<SystemUser> wrapper = new LambdaQueryWrapper<>();
        if (queryVo != null) {
            if (StringUtils.hasText(queryVo.getKeyword())) {
                wrapper.and(w -> w
                        .like(SystemUser::getUsername, queryVo.getKeyword())
                        .or()
                        .like(SystemUser::getName, queryVo.getKeyword())
                );
            }
            if (queryVo.getType() != null) {
                wrapper.eq(SystemUser::getType, queryVo.getType());
            }
            if (queryVo.getStatus() != null) {
                wrapper.eq(SystemUser::getStatus, queryVo.getStatus());
            }
        }
        wrapper.orderByDesc(SystemUser::getCreateTime);

        IPage<SystemUser> userPage = systemUserMapper.selectPage(
                new Page<>(page.getCurrent(), page.getSize()), wrapper
        );

        // 批量查岗位名称
        List<Long> postIds = userPage.getRecords().stream()
                .map(SystemUser::getPostId)
                .filter(id -> id != null)
                .distinct()
                .toList();
        final Map<Long, String> postNameMap;
        if (!postIds.isEmpty()) {
            List<SystemPost> posts = systemPostMapper.selectBatchIds(postIds);
            postNameMap = posts.stream()
                    .collect(Collectors.toMap(SystemPost::getId, SystemPost::getName));
        } else {
            postNameMap = Map.of();
        }

        // 转 VO
        List<SystemUserItemVo> voList = userPage.getRecords().stream().map(u -> {
            SystemUserItemVo vo = new SystemUserItemVo();
            BeanUtils.copyProperties(u, vo);
            vo.setPostName(u.getPostId() != null ? postNameMap.getOrDefault(u.getPostId(), "") : "");
            return vo;
        }).toList();

        IPage<SystemUserItemVo> result = new Page<>(page.getCurrent(), page.getSize(), userPage.getTotal());
        result.setRecords(voList);
        return result;
    }

    @Override
    public SystemUserInfoVo getUserById(Long id) {
        SystemUser user = systemUserMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
        }
        SystemUserInfoVo vo = new SystemUserInfoVo();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }

    @Override
    @Transactional
    public void saveUser(SystemUserInfoVo vo) {
        // 用户名唯一检查
        Long count = systemUserMapper.selectCount(
                new LambdaQueryWrapper<SystemUser>().eq(SystemUser::getUsername, vo.getUsername())
        );
        if (count > 0) {
            throw new BusinessException(ResultCodeEnum.BAD_REQUEST.getCode(), "用户名已存在");
        }

        SystemUser user = new SystemUser();
        BeanUtils.copyProperties(vo, user);
        user.setPassword(passwordEncoder.encode(vo.getPassword()));
        systemUserMapper.insert(user);
        log.info("新增用户: {}", vo.getUsername());
    }

    @Override
    @Transactional
    public void updateUser(SystemUserInfoVo vo) {
        SystemUser user = systemUserMapper.selectById(vo.getId());
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
        }

        BeanUtils.copyProperties(vo, user);
        // 如果传了新密码则加密
        if (StringUtils.hasText(vo.getPassword())) {
            user.setPassword(passwordEncoder.encode(vo.getPassword()));
            // 改密码 = 踢所有设备
            jwtTokenService.kickAll(vo.getId());
        }
        systemUserMapper.updateById(user);
        log.info("更新用户: id={}", vo.getId());
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        SystemUser user = systemUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCodeEnum.USER_NOT_FOUND);
        }
        systemUserMapper.deleteById(userId);
        jwtTokenService.kickAll(userId);
        log.info("删除用户: id={}", userId);
    }

    @Override
    public void kickUser(Long userId) {
        jwtTokenService.kickAll(userId);
        log.info("踢出用户: id={}", userId);
    }

}
